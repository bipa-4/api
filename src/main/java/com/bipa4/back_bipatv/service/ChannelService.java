package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.AccountDAO;
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

  public SelectChannelDTO findChannel(String accessToken, Long channelId) {
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

  public SelectChannelDTO findChannel(Long channelId) {

    SelectChannelDTO selectChannelDTO = channelRepository.selectChannel(channelId);

    selectChannelDTO.setUpdateFlag(false);
    return selectChannelDTO;

  }

  public Channels findbyChannelId(Long channelId) {
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


  public List<GetChannelDTO> getAllChannels() {
    return channelRepository.getNotPrivateChannel();
  }

  @Transactional
  public Channels updateChannel(Long chnnaelId, String code, PutChannelDTO putChannelDTO) {
    Accounts loginAccount = securityService.getSubjectAccount(code);
    AccountDAO accountDAO = new AccountDAO();
    Accounts putAccount = accountDAO.selectAccount(putChannelDTO.getAccounts());
    if (!Objects.equals(loginAccount.getAccountId(),
        putAccount.getAccountId())) { //로그인한 accountId와 수정할 채널의 accountId가 같은 경우
      throw new ResourceNotFoundException(
          "로그인 유저와 수정할 채널의 유저 정보가 같지 않다.");
    }
    Channels myChannel = findbyChannelId(chnnaelId);
    if (!myChannel.getContent().equals(putChannelDTO.getContent())) {
      myChannel.setContent(putChannelDTO.getContent());
    }
    if (!myChannel.getProfileUrl().equals(putChannelDTO.getProfileUrl())) {
      myChannel.setProfileUrl(putChannelDTO.getProfileUrl());
    }
    if (!(myChannel.isPrivateType() == putChannelDTO.isPrivateType())) {
      myChannel.setPrivateType(putChannelDTO.isPrivateType());
    }
    if (!(myChannel.getChannelName().equals(putChannelDTO.getChannelName()))) {
      myChannel.setChannelName(putChannelDTO.getChannelName());
    }
    return channelRepository.save(myChannel);
  }

  public List<GetVideoResponseDto> getVideosInChannel(Long channelId) {
    return videoRepository.getVideosInChannel(channelId);
  }
}
