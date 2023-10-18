package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dataType.ErrorCode;
import com.bipa4.back_bipatv.dto.CustomApiException;
import com.bipa4.back_bipatv.dto.channel.GetChannelDTO;
import com.bipa4.back_bipatv.dto.channel.GetChannelTop5DTO;
import com.bipa4.back_bipatv.dto.channel.GetInfiniteScrollRequestChannelDto;
import com.bipa4.back_bipatv.dto.channel.SelectChannelDTO;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.dto.video.GetCategoryNameRequestDto;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetInfiniteScrollRequestDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.ChannelService;
import com.bipa4.back_bipatv.service.CommentService;
import com.bipa4.back_bipatv.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = {"ReadController"})
@RequiredArgsConstructor
@Controller
public class ReadController {

  private final VideoService videoService;
  private final CommentService commentService;
  private final ChannelService channelService;
  private final SecurityService securityService;


  // 전체 조회 (최신 순으로)
  @ApiOperation(value = "전체 조회", notes = "최신순으로 전체 조회 (무한스크롤) / 처음엔 page 안넘겨주면 됨.")
  @GetMapping("/video/latest")
  public ResponseEntity<GetInfiniteScrollRequestDto> getAllVideos(
      @RequestParam(value = "page", required = false) String page,
      @RequestParam("pageSize") int pageSize) {
    List<GetVideoResponseDto> videos = videoService.getAllVideos(page, pageSize);
    if (videos == null) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }
    String nextUUID = videoService.getNextUUID(
        videos.get(videos.size() - 1).getVideoId()); // 마지막 page의 UUID 호출
    GetInfiniteScrollRequestDto responseDto = new GetInfiniteScrollRequestDto(videos, nextUUID);

    return ResponseEntity.ok().body(responseDto);
  }


  // 카테고리별 조회
  @ApiOperation(value = "카테고리별 전체 조회", notes = "카테고리 별 전체 조회 (무한스크롤)")
  @GetMapping("/video/category/{category}")
  public ResponseEntity<GetInfiniteScrollRequestDto> getCategoryVideos(
      @PathVariable("category") UUID category,
      @RequestParam(value = "page", required = false) String page,
      @RequestParam("pageSize") int pageSize) {
    List<GetVideoResponseDto> videos = videoService.getCategoryVideos(category, page, pageSize);
    if (videos == null) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }
    String nextUUID = videoService.getNextUUID(
        videos.get(videos.size() - 1).getVideoId()); // 마지막 page의 UUID 호출
    GetInfiniteScrollRequestDto responseDto = new GetInfiniteScrollRequestDto(videos, nextUUID);
    return ResponseEntity.ok().body(responseDto);
  }


  // 카테고리 이름 리스트 조회
  @ApiOperation(value = "카테고리 리스트 조회", notes = "카테고리 메뉴 등을 위한 카테고리 이름 추출")
  @GetMapping("/video/category")
  public ResponseEntity<List<GetCategoryNameRequestDto>> getCategoryNames() {
    List<GetCategoryNameRequestDto> categorys = videoService.getCategoryNames();
    if (categorys == null) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }
    return new ResponseEntity<List<GetCategoryNameRequestDto>>(categorys, HttpStatus.OK);
  }


  // 조회수 급상승 TOP 10
  @ApiOperation(value = "조회수 급상승 TOP 10", notes = "1시간마다 조회수가 급상승된 영상 10개 추출")
  @GetMapping("/video/top10")
  public ResponseEntity<List<GetVideoResponseDto>> getViewsTop10Videos() {
    List<GetVideoResponseDto> videos = videoService.getViewsTop10Videos();
    if (videos == null) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }
    return new ResponseEntity<List<GetVideoResponseDto>>(videos, HttpStatus.OK);
  }


  // 디비 1시간 전 정보 저장
  @ApiOperation(value = "조회수 급상승 TOP 10", notes = "1시간마다 조회수가 급상승된 영상 10개 추출")
  @Scheduled(cron = "0 0 0/1 * * *")
  public ResponseEntity<Boolean> getViewsUpdate() {
    if (videoService.updateViews() == 0) {
      throw new CustomApiException(ErrorCode.UPDATE_ERROR);
    }
    return new ResponseEntity<>(true, HttpStatus.OK);
  }


  // 영상 상세 조회
  @ApiOperation(value = "영상 상세 조회 및 추천 영상 조회", notes = "영상 클릭시 상세 정보 + 추천 영상 추출")
  @GetMapping("/video/detail/{videoId}")
  public ResponseEntity<GetDetailResponseDto> getVideoDetail(
      @PathVariable("videoId") UUID id) {
    int viewsResult = videoService.plusViews(id); //  조회수 상승
    GetDetailResponseDto video = videoService.getVideoDetail(id);

    if (video == null) {
      throw new CustomApiException(ErrorCode.READ_ERROR);
    }
    if (viewsResult == 0) {
      throw new CustomApiException(ErrorCode.UPDATE_ERROR);
    }
    return new ResponseEntity<>(video, HttpStatus.OK);
  }


  // 영상 좋아요 여부
  @ApiOperation(value = "영상 상세 조회 및 추천 영상 조회", notes = "영상 클릭시 상세 정보 + 추천 영상 추출")
  @GetMapping("/video/like/{videoId}")
  public ResponseEntity<Boolean> getVideoLike(
      @PathVariable("videoId") UUID id,
      @CookieValue(name = "accessToken", required = false) String accessToken) {
    return new ResponseEntity<>(videoService.getLike(id, accessToken), HttpStatus.OK);
  }


  //부모 댓글 조회
  @ApiOperation(value = "부모 댓글 조회", notes = "부모 댓글 조회")
  @GetMapping("/comment/{videoId}/comment-parent")
  public ResponseEntity<List<CommentResponse>> findParentComments(@PathVariable UUID videoId) {
    List<CommentResponse> list = commentService.findParentComments(videoId);
    return ResponseEntity.ok(list);
  }

  //자식 댓글 조회
  @ApiOperation(value = "자식 댓글 조회", notes = "자식 댓글 조회")
  @GetMapping("/comment/{videoId}/comment-child")
  public ResponseEntity<List<CommentResponse>> findChildComments(@PathVariable UUID videoId,
      @RequestParam int groupIndex) {
    List<CommentResponse> list = commentService.findChildComments(videoId, groupIndex);

    return ResponseEntity.ok(list);
  }


  // 나의 채널 정보 조회
  @ApiOperation(value = "채널 상세 조회", notes = "채널 상세 조회")
  @GetMapping("/channel/{channelId}")
  public ResponseEntity<SelectChannelDTO> getMyChannelInfo(
      @PathVariable("channelId") UUID channelId) {
    SelectChannelDTO selectChannelDTO = channelService.findChannel(channelId);
    if (selectChannelDTO != null) {
      return new ResponseEntity<>(selectChannelDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
  }

  @ApiOperation(value = "getUpdateFlag", notes = "업데이트 플레그 얻기")
  @GetMapping("/channel/flag/{channelId}")
  public ResponseEntity<Boolean> getUpdateFlag(
      @CookieValue(value = "accessToken", required = false) String accessToken,
      @PathVariable("channelId") UUID channelId) {
    if (accessToken == null) {//비회원 채널 조회
      return new ResponseEntity<>(false, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(channelService.getUpdateFlag(accessToken, channelId),
          HttpStatus.OK);
    }
  }

  // 인기 채널 top 10 조회
  @ApiOperation(value = "실시간 인기 채널 5", notes = "가장 인기 있는 채널 TOP5을 들고온다")
  @GetMapping("/channel/top5")
  public ResponseEntity<List<GetChannelTop5DTO>> getViewsTop10Channels() {
    List<GetChannelTop5DTO> channels = channelService.findLimitTimeSumCnt();
    channels.forEach(System.out::println);
    return new ResponseEntity<List<GetChannelTop5DTO>>(channels, HttpStatus.OK);
  }


  // 전체 채널 조회
  @ApiOperation(value = "전체 채널 조회", notes = "전체 채널에 대한 정보")
  @GetMapping("/channel/AllChannel")
  public ResponseEntity<GetInfiniteScrollRequestChannelDto> getAllChannels(
      @RequestParam(value = "page", required = false) String page,
      @RequestParam("pageSize") int pageSize) {

    List<GetChannelDTO> list = channelService.getAllChannels(page, pageSize);
    String nextUUID = channelService.getNextUUID(list.get(list.size() - 1).getChannelId());

    GetInfiniteScrollRequestChannelDto channelList = new GetInfiniteScrollRequestChannelDto(list,
        nextUUID);

    list.forEach(System.out::println);
    return new ResponseEntity<>(channelList, HttpStatus.OK);
  }

  @ApiOperation(value = "채널 내 영상 조회", notes = "최신순으로 전체 조회 (무한스크롤) / 처음엔 page 안넘겨주면 됨.")
  @GetMapping("/channel/video/{channelId}")
  public ResponseEntity<GetInfiniteScrollRequestDto> getVideosInChannel(
      @RequestParam(value = "page", required = false) String page,
      @RequestParam("pageSize") int pageSize,
      @PathVariable("channelId") UUID channelId) {
    List<GetVideoResponseDto> videos = channelService.getVideosInChannel(channelId, page, pageSize);
    String nextUUID = videoService.getNextUUID(
        videos.get(videos.size() - 1).getVideoId()); // 마지막 page의 UUID 호출
    GetInfiniteScrollRequestDto responseDto = new GetInfiniteScrollRequestDto(videos, nextUUID);

    return ResponseEntity.ok().body(responseDto);
  }


}
