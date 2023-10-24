package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.ChannelDAO;
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

  private final ChannelDAO channelDAO;
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

  public UUID getChannelNextUUID(UUID uuid) {
    return channelRepository.getChannelNextUUID(uuid);
  }

  public List<GetChannelDTO> getAllChannels(UUID page, int pageSize) {
    UUID uuid;
    if (page == null) {
      uuid = channelRepository.lastUUID();

    } else {
      uuid = page;
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
      UUID page, int pageSize) {
    /**
     * 1. login 한 user 인경우
     * 2. login 했는데 admin 인경우
     * 3. login 하지않은 비회원인경우
     *
     * 로직순서 :
     * 1. accesstoken 을 검사한다.
     *    있냐/없냐 => 회원/비회원 확인
     * 2. 채널의 account 값이랑 login 한 account 값이랑 비교하여 확인.
     * 3. Data 불러와서 return
     */

    Accounts loginUser = null;
    if (accessToken != null) {
      loginUser = securityService.getSubjectAccount(accessToken);
    }
    UUID uuid = null;
    if (Objects.isNull(loginUser)) {
      System.out.println("비회원");
      if (page == null) {
        uuid = videoRepository.lastUUIDInChannel(channelId);
      } else {
        uuid = page;
      }
      videoRepository.getVideosInChannel(channelId, uuid, pageSize).forEach(System.out::println);
      return videoRepository.getVideosInChannel(channelId, uuid, pageSize);
    }
    if (loginUser.getAccountId().equals(channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {//채널 주인이 로그인한 사람이면 채널 내 비공개 영상도 조회 가능해야 함
      if (page == null) {
        uuid = videoRepository.lastUUIDInMyChannel(channelId);
      } else {
        uuid = page;
      }
      System.out.println("mychannel들어옴");
      videoRepository.getVideosInMyChannel(channelId, uuid, pageSize)
          .forEach(System.out::println);
      return videoRepository.getVideosInMyChannel(channelId, uuid, pageSize);
    }
    if (page == null) {
      uuid = videoRepository.lastUUIDInChannel(channelId);
    } else {
      uuid = page;
    }
    videoRepository.getVideosInChannel(channelId, uuid, pageSize).forEach(System.out::println);
    return videoRepository.getVideosInChannel(channelId, uuid, pageSize);
  }

  public UUID getNextChannelVideoUUID(UUID videoId, UUID channelId, String accessToken) {
    Accounts loginUser = null;
    if (accessToken != null) {
      loginUser = securityService.getSubjectAccount(accessToken);
    }
    if (loginUser == null) {
      return channelRepository.getNextChannelVideoUUID(videoId, channelId, false);
    }
    if (loginUser.getAccountId() == (channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {//채널 주인이 로그인한 사람이면 채널 내 비공개 영상도 조회 가능해야 함
      return channelRepository.getNextChannelVideoUUID(videoId, channelId, true);
    } else {
      return channelRepository.getNextChannelVideoUUID(videoId, channelId, false);
    }
  }

  public Integer getSearchNextChannelVideoUUID(Integer rank, UUID channelId,
      String searchQuery,
      String accessToken, int pageSize, Integer page) {
    Accounts loginUser = null;
    List<Integer> nextVideoInChannelRank = null;
    if (accessToken != null) {
      loginUser = securityService.getSubjectAccount(accessToken);
    }
    if (Objects.isNull(loginUser)) {
      System.out.println("비회원");
      nextVideoInChannelRank = channelRepository.getSearchNextChannelVideoRank(rank,
          channelId, searchQuery, pageSize, page);
      if (nextVideoInChannelRank.isEmpty()) {
        return null;//null로 바꾸기
      } else {
        return nextVideoInChannelRank.get(0);
      }
    }
    if (loginUser.getAccountId().equals(channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {//채널 주인이 로그인한 사람이면 채널 내 비공개 영상도 조회 가능해야 함
      nextVideoInChannelRank = channelRepository.getSearchNextMyChannelVideoRank(rank,
          channelId, searchQuery, pageSize, page);
      if (nextVideoInChannelRank.isEmpty()) {
        return null;//null로 바꾸기
      } else {
        return nextVideoInChannelRank.get(0);
      }
    } else {
      nextVideoInChannelRank = channelRepository.getSearchNextChannelVideoRank(rank,
          channelId, searchQuery, pageSize, page);
      if (nextVideoInChannelRank.isEmpty()) {
        return null;//null로 바꾸기
      } else {
        return nextVideoInChannelRank.get(0);
      }
    }
  }

  public Integer getNextChannelRank(String searchQuery, Integer ranking, int pageSize,
      Integer page) {
    List<Integer> nextChannelRank = channelRepository.getNextChannelRank(searchQuery, ranking,
        pageSize, page);
    if (nextChannelRank.isEmpty()) {
      return null;//null로 바꾸기
    } else {
      return nextChannelRank.get(0);
    }
  }

  public List<GetSearchVideoINChannelDTO> searchVideoInChannel(String accessToken, UUID channelId,
      Integer page, int pageSize, String searchQuery) {
    Accounts loginUser = null;
    if (accessToken != null) {
      loginUser = securityService.getSubjectAccount(accessToken);
    }
    Integer currentPage = null;
    if (Objects.isNull(loginUser)) {
      if (page == null) {
        currentPage =
            videoRepository.lastUUIDSearchVideoInChannel(channelId, searchQuery).get(0) == null
                ? null
                : videoRepository.lastUUIDSearchVideoInChannel(channelId, searchQuery).get(0);
      } else {
        currentPage = page;
      }
      System.out.println("채널내 영상 검색 UUID 값:" + currentPage);
      videoRepository.getSearchVideoInChannel(channelId, currentPage, pageSize, searchQuery)
          .forEach(
              System.out::println);
      return videoRepository.getSearchVideoInChannel(channelId, currentPage, pageSize, searchQuery);
    }

    if (loginUser.getAccountId().equals(channelRepository.findByChannelId(channelId).getAccounts()
        .getAccountId())) {
      if (page == null) {
        currentPage =
            videoRepository.lastUUIDSearchVideoInMyChannel(channelId, searchQuery).get(0) == null
                ? null
                : videoRepository.lastUUIDSearchVideoInMyChannel(channelId, searchQuery).get(0);
      } else {
        currentPage = page;
      }

      return videoRepository.getSearchVideoInMyChannel(channelId, currentPage, pageSize,
          searchQuery);

    } else {
      if (page == null) {
        currentPage =
            videoRepository.lastUUIDSearchVideoInChannel(channelId, searchQuery).get(0) == null
                ? null
                : videoRepository.lastUUIDSearchVideoInChannel(channelId, searchQuery).get(0);
      } else {
        currentPage = page;
      }

      videoRepository.getSearchVideoInChannel(channelId, currentPage, pageSize, searchQuery)
          .forEach(
              System.out::println);
      return videoRepository.getSearchVideoInChannel(channelId, currentPage, pageSize, searchQuery);

    }
  }

  public List<GetSearchChannelDTO> searchChannel(Integer page, int pageSize, String searchQuery) {
    Integer currentPage = null;
    System.out.println("page값 " + page);
    if (page == null) {
      currentPage =
          channelRepository.lastUUIDSearchChannel(searchQuery).get(0) == null
              ? null
              : channelRepository.lastUUIDSearchChannel(searchQuery).get(0);
    } else {
      currentPage = page;
    }
    System.out.println("searchChannel Method currentPage = " + currentPage);
    channelRepository.getSearchChannel(currentPage, pageSize, searchQuery).forEach(
        System.out::println);
    return channelRepository.getSearchChannel(currentPage, pageSize, searchQuery);
  }


}
