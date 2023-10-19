package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.ChannelDAO;
import com.bipa4.back_bipatv.dto.channel.GetChannelDTO;
import com.bipa4.back_bipatv.dto.channel.GetChannelTop5DTO;
import com.bipa4.back_bipatv.dto.channel.PutChannelDTO;
import com.bipa4.back_bipatv.dto.channel.SelectChannelDTO;
import com.bipa4.back_bipatv.dto.video.GetSearchVideoINChannelDTO;
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

  public boolean getUpdateFlag(String accessToken, UUID channelId) {
    Accounts loginAccount = securityService.getSubjectAccount(accessToken);
    Channels selectChannel = channelRepository.findByChannelId(channelId);

    if (Objects.equals(selectChannel.getAccounts().getAccountId(),
        loginAccount.getAccountId())) {//수정 가능
      return true;
    } else {//수정 불가
      return false;
    }
  }

  public SelectChannelDTO findChannel(UUID channelId) {
    return channelRepository.selectChannel(channelId);
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

  public String getChannelNextUUID(UUID uuid) {
    return channelRepository.getChannelNextUUID(uuid);
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

  public List<GetVideoResponseDto> getVideosInChannel(String accessToken, UUID channelId,
      String page, int pageSize) {
    Accounts loginUser = securityService.getSubjectAccount(accessToken);
    UUID uuid = null;
    if (loginUser.getAccountId() == (channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {//채널 주인이 로그인한 사람이면 채널 내 비공개 영상도 조회 가능해야 함
      if (page == null) {
        uuid = videoRepository.lastUUIDInMyChannel(channelId);
      } else {
        uuid = UUID.fromString(page);
      }
      videoRepository.getVideosInMyChannel(channelId, uuid, pageSize).forEach(System.out::println);
      return videoRepository.getVideosInMyChannel(channelId, uuid, pageSize);
    } else {
      if (page == null) {
        uuid = videoRepository.lastUUIDInChannel(channelId);
      } else {
        uuid = UUID.fromString(page);
      }
      videoRepository.getVideosInChannel(channelId, uuid, pageSize).forEach(System.out::println);
      return videoRepository.getVideosInChannel(channelId, uuid, pageSize);
    }
  }

  public String getNextChannelVideoUUID(UUID videoId, UUID channelId, String accessToken) {
    Accounts loginUser = securityService.getSubjectAccount(accessToken);
    if (loginUser.getAccountId() == (channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {//채널 주인이 로그인한 사람이면 채널 내 비공개 영상도 조회 가능해야 함
      return channelRepository.getNextChannelVideoUUID(videoId, channelId, true);
    } else {
      return channelRepository.getNextChannelVideoUUID(videoId, channelId, false);
    }
  }

  public String getSearchNextChannelVideoUUID(UUID videoId, UUID channelId,
      String searchQuery,
      String accessToken) {
    Accounts loginUser = securityService.getSubjectAccount(accessToken);
    if (loginUser.getAccountId() == (channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {//채널 주인이 로그인한 사람이면 채널 내 비공개 영상도 조회 가능해야 함
      List<String> nextChannelUUID = channelRepository.getSearchNextChannelVideoUUID(videoId,
          channelId, searchQuery, true);
      if (nextChannelUUID.isEmpty()) {
        return null;//null로 바꾸기
      } else {
        return nextChannelUUID.get(0);
      }
    } else {
      List<String> nextChannelUUID = channelRepository.getSearchNextChannelVideoUUID(videoId,
          channelId, searchQuery, false);
      if (nextChannelUUID.isEmpty()) {
        return null;//null로 바꾸기
      } else {
        return nextChannelUUID.get(0);
      }
    }
  }

  public List<GetSearchVideoINChannelDTO> searchVideoInChannel(String accessToken, UUID channelId,
      String page, int pageSize, String searchQuery) {
    Accounts loginUser = securityService.getSubjectAccount(accessToken);
    UUID uuid = null;
    System.out.println("page값 " + page);
    if (loginUser.getAccountId() == (channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {
      if (page == null) {
        uuid = videoRepository.lastUUIDSearchVideoInMyChannel(channelId, searchQuery).get(0);
      } else {
        uuid = UUID.fromString(page);
      }
      System.out.println("채널내 영상 검색 UUID 값:" + uuid);

      return videoRepository.getSearchVideoInMyChannel(channelId, uuid, pageSize, searchQuery);
    } else {
      if (page == null) {
        uuid = videoRepository.lastUUIDSearchVideoInChannel(channelId, searchQuery).get(0);
      } else {
        uuid = UUID.fromString(page);
      }
      System.out.println("채널내 영상 검색 UUID 값:" + uuid);

      return videoRepository.getSearchVideoInChannel(channelId, uuid, pageSize, searchQuery);
    }
  }
}
