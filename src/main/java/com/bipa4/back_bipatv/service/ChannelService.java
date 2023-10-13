package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.ChannelDAO;
import com.bipa4.back_bipatv.dto.channel.GetChannelDTO;
import com.bipa4.back_bipatv.dto.channel.GetChannelTop5DTO;
import com.bipa4.back_bipatv.dto.channel.PutChannelDTO;
import com.bipa4.back_bipatv.dto.channel.SelectChannelDTO;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.exception.ResourceNotFoundException;
import com.bipa4.back_bipatv.repository.ChannelRepository;
import com.bipa4.back_bipatv.repository.VideoRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChannelService {

  private final ChannelDAO channelDAO;
  private final SecurityService securityService;
  private final ChannelRepository channelRepository;
  private final VideoRepository videoRepository;

  public SelectChannelDTO findChannel(String accessToken, UUID channelId) {
    Accounts loginAccount = securityService.getSubjectAccount(accessToken);

    Channels selectChannel = channelRepository.findByChannelId(channelId);
    SelectChannelDTO selectChannelDTO = channelRepository.selectChannel(channelId);
    System.out.println(selectChannelDTO);
    if (Objects.equals(selectChannel.getAccounts().getAccountId(),
        loginAccount.getAccountId())) {//수정 가능
      selectChannelDTO.setUpdateFlag(true);
      return selectChannelDTO;
    } else {//수정 불가
      selectChannelDTO.setUpdateFlag(false);
      return selectChannelDTO;
    }
  }

  public SelectChannelDTO findChannel(UUID channelId) {

    SelectChannelDTO selectChannelDTO = channelRepository.selectChannel(channelId);

    selectChannelDTO.setUpdateFlag(false);
    return selectChannelDTO;

  }

  public Channels findbyChannelId(UUID channelId) {
    return channelRepository.findByChannelId(channelId);
  }

  public List<GetChannelTop5DTO> findLimitTimeSumCnt() {
    List<GetChannelTop5DTO> list = channelDAO.findTop5Channels();

    IntStream.range(0, list.size())
        .forEach(i -> list.get(i).setRanking(i + 1));
    return list;
  }

  public List<GetChannelTop5DTO> findLimitTimeSumCntV2() {
    List<GetChannelTop5DTO> list = channelDAO.findTop5Channels();

    int i = 0;
    list.get(0).setRanking(1);
    for (GetChannelTop5DTO item : list) {
      i++;
      if (Objects.equals(list.get(i - 1).getTimeLimitSumCnt(), list.get(i).getTimeLimitSumCnt())) {
        list.get(i).setRanking(item.getRanking());
      } else {
        list.get(i).setRanking(i + 1);
      }
      if (i == list.size() - 1) {
        break;
      }
    }
    return list;
  }

  public String getNextUUID(UUID uuid) {
    return videoRepository.getNextUUID(uuid);
  }

  public List<GetChannelDTO> getAllChannels(String page, int pageSize) {
    UUID uuid;
    if (page == null) {
      uuid = channelRepository.lastUUID();

    } else {
      uuid = UUID.fromString(page);
    }
    System.out.println("Service getAllChannels Method uuid: " + uuid);
    return channelRepository.getNotPrivateChannel(uuid, pageSize);
  }

  @Transactional
  public Channels updateChannel(UUID channelId, String code, PutChannelDTO putChannelDTO) {
    Accounts loginAccount = securityService.getSubjectAccount(code);

    Accounts putAccount = channelRepository.findByChannelId(channelId).getAccounts();

    System.out.println("loginAccount:" + loginAccount.getAccountId());
    System.out.println("putAccount:" + putAccount.getAccountId());

    if (!Objects.equals(loginAccount.getAccountId(),
        putAccount.getAccountId())) { //로그인한 accountId와 수정할 채널의 accountId가 같은 경우
      throw new ResourceNotFoundException(
          "로그인 유저와 수정할 채널의 유저 정보가 같지 않다.");
    }
    Channels myChannel = findbyChannelId(channelId);
    System.out.println(myChannel);
    System.out.println(putChannelDTO);
    if (!(myChannel.getContent()
        .equals(putChannelDTO.getContent()))) {
      myChannel.setContent(putChannelDTO.getContent());
    }
    if (!(myChannel.getProfileUrl()
        .equals(putChannelDTO.getProfileUrl()))) {
      myChannel.setProfileUrl(putChannelDTO.getProfileUrl());
    }
    if (!(myChannel.getPrivateType().equals(putChannelDTO.isPrivateType()))) {
      myChannel.setPrivateType(putChannelDTO.isPrivateType());
    }
    if (!(myChannel.getChannelName()
        .equals(putChannelDTO.getChannelName()))) {
      myChannel.setChannelName(putChannelDTO.getChannelName());
    }
    return channelRepository.save(myChannel);
  }

  public List<GetVideoResponseDto> getVideosInChannel(UUID channelId, String page, int pageSize) {
    UUID uuid = null;
    if (page == null) {
      uuid = videoRepository.lastUUIDInChannel(channelId);
    } else {
      uuid = UUID.fromString(page);
    }
    return videoRepository.getVideosInChannel(channelId, uuid, pageSize);
  }
}
