package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dataType.ErrorCode;
import com.bipa4.back_bipatv.dto.channel.GetChannelDTO;
import com.bipa4.back_bipatv.dto.channel.GetChannelTop5DTO;
import com.bipa4.back_bipatv.dto.channel.GetSearchChannelDTO;
import com.bipa4.back_bipatv.dto.channel.PutChannelDTO;
import com.bipa4.back_bipatv.dto.channel.SelectChannelDTO;
import com.bipa4.back_bipatv.dto.video.GetSearchVideoINChannelDTO;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.exception.CustomApiException;
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

  private final SecurityService securityService;
  private final ChannelRepository channelRepository;
  private final VideoRepository videoRepository;

  public boolean getUpdateFlag(Accounts accounts, UUID channelId) {
    Channels selectChannel;

    //비회원 채널 조회
    if (accounts == null) {
      return false;
    }

    try {
      selectChannel = channelRepository.findByChannelId(channelId);
    } catch (Exception e) {
      throw new CustomApiException(ErrorCode.NO_EXIST_CHANNEL);
    }

    if (selectChannel == null) {
      throw new CustomApiException(ErrorCode.NO_EXIST_CHANNEL);
    }

    if (!Objects.equals(selectChannel.getAccounts().getAccountId(),
        accounts.getAccountId())) {
      return false;
    }

    return true;
  }

  public SelectChannelDTO findChannel(UUID channelId) {
    return channelRepository.selectChannel(channelId);
  }

  public Channels findbyChannelId(UUID channelId) {
    return channelRepository.findByChannelId(channelId);
  }

  public List<GetChannelTop5DTO> findLimitTimeSumCnt() {
    List<GetChannelTop5DTO> list = channelRepository.findTop5Channels();

    IntStream.range(0, list.size())
        .forEach(i -> list.get(i).setRanking(i + 1));
    return list;
  }

  public UUID getChannelNextUUID(UUID uuid) {
    return channelRepository.getChannelNextUUID(uuid);
  }

  public List<GetChannelDTO> getAllChannels(UUID page, int pageSize) {
    if (page == null) {
      page = channelRepository.lastUUID();
    }
    return channelRepository.getNotPrivateChannel(page, pageSize);
  }

  @Transactional
  public Channels updateChannel(UUID channelId, String code, PutChannelDTO putChannelDTO) {
    Accounts loginAccount = securityService.getSubjectAccount(code);

    Accounts putAccount = channelRepository.findByChannelId(channelId).getAccounts();

    if (!Objects.equals(loginAccount.getAccountId(),
        putAccount.getAccountId())) { //로그인한 accountId와 수정할 채널의 accountId가 같은 경우
      throw new ResourceNotFoundException(
          "로그인 유저와 수정할 채널의 유저 정보가 같지 않다.");
    }
    Channels myChannel = findbyChannelId(channelId);

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

  public List<GetVideoResponseDto> getVideosInChannel(Accounts account, UUID channelId,
      UUID page, int pageSize) {
    // 비회원
    if (account == null) {
      if (page == null) {
        page = videoRepository.lastUUIDInChannel(channelId);
      }
      return videoRepository.getVideosInChannel(channelId, page, pageSize);
    }

    // 본인 채널
    if (account.getAccountId()
        .equals(channelRepository.findByChannelId(channelId).getAccounts()
            .getAccountId())) {//채널 주인이 로그인한 사람이면 채널 내 비공개 영상도 조회 가능해야 함
      if (page == null) {
        page = videoRepository.lastUUIDInMyChannel(channelId);
      }
      return videoRepository.getVideosInMyChannel(channelId, page, pageSize);
    }

    // 그외 채널
    if (page == null) {
      page = videoRepository.lastUUIDInChannel(channelId);
    }

    return videoRepository.getVideosInChannel(channelId, page, pageSize);
  }

  public UUID getNextChannelVideoUUID(UUID videoId, UUID channelId, Accounts account) {
    if (account == null) { // 비회원
      return channelRepository.getNextChannelVideoUUID(videoId, channelId, false);
    }
    if (account.getAccountId() == channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId()) { // 본인 채널
      return channelRepository.getNextChannelVideoUUID(videoId, channelId, true);
    } else { // 다른 회원
      return channelRepository.getNextChannelVideoUUID(videoId, channelId, false);
    }
  }

  public Integer getSearchNextChannelVideoUUID(Integer rank, UUID channelId,
      String searchQuery,
      Accounts account, int pageSize, Integer page) {
    if (account == null) {
      return channelRepository.getSearchNextChannelVideoRank(rank,
          channelId, searchQuery, pageSize, page);
    }
    if (account.getAccountId().equals(channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {//채널 주인이 로그인한 사람이면 채널 내 비공개 영상도 조회 가능해야 함
      return channelRepository.getSearchNextMyChannelVideoRank(rank,
          channelId, searchQuery, pageSize, page);
    }
    return channelRepository.getSearchNextChannelVideoRank(rank,
        channelId, searchQuery, pageSize, page);
  }

  public Integer getNextChannelRank(String searchQuery, Integer ranking, int pageSize,
      Integer page) {
    return channelRepository.getNextChannelRank(searchQuery, ranking,
        pageSize, page);
  }

  public List<GetSearchVideoINChannelDTO> searchVideoInChannel(Accounts account, UUID channelId,
      Integer page, int pageSize, String searchQuery) {

    // 비회원
    if (account == null) {
      if (page == null) {
        page = videoRepository.lastUUIDSearchVideoInChannel(channelId, searchQuery);
      }
      return videoRepository.getSearchVideoInChannel(channelId, page, pageSize, searchQuery);
    }

    // 본인 글
    if (account.getAccountId().equals(channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {
      if (page == null) {
        page = videoRepository.lastUUIDSearchVideoInChannel(channelId, searchQuery);
      }
      return videoRepository.getSearchVideoInMyChannel(channelId, page, pageSize, searchQuery);
    }

    // 본인 글이 아닌 경우
    if (page == null) {
      page = videoRepository.lastUUIDSearchVideoInChannel(channelId, searchQuery);
    }
    return videoRepository.getSearchVideoInChannel(channelId, page, pageSize, searchQuery);
  }

  public List<GetSearchChannelDTO> searchChannel(Integer page, int pageSize, String searchQuery) {
    if (page == null) {
      page = channelRepository.lastUUIDSearchChannel(searchQuery);
    }
    return channelRepository.getSearchChannel(page, pageSize, searchQuery);
  }
}
