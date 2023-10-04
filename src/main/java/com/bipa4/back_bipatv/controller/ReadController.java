package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.channel.CustomChannelTop10;
import com.bipa4.back_bipatv.dto.comment.CommentResponse;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.ChannelService;
import com.bipa4.back_bipatv.service.CommentService;
import com.bipa4.back_bipatv.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = {"ReadController"})
@RequestMapping("/read")
@RequiredArgsConstructor
@Controller
public class ReadController {

  private final VideoService videoService;
  private final CommentService commentService;
  private final ChannelService channelService;
  private final SecurityService securityService;


  // 전체 조회 (최신 순으로)
  @ApiOperation(value = "전체 조회", notes = "최신순으로 전체 조회 (무한스크롤)")
  @GetMapping("/video/latest")
  public ResponseEntity<List<GetVideoResponseDto>> getAllVideos(@RequestParam("page") int page,
      @RequestParam("pageSize") int pageSize) {
    List<GetVideoResponseDto> videos = videoService.getAllViideos(page, pageSize);
    return new ResponseEntity<List<GetVideoResponseDto>>(videos, HttpStatus.OK);
  }


  // 카테고리별 조회
  @ApiOperation(value = "카테고리별 전체 조회", notes = "카테고리 별 전체 조회 (무한스크롤)")
  @GetMapping("/video/category/{category}")
  public ResponseEntity<List<GetVideoResponseDto>> getCategoryVideos(
      @PathVariable("category") String category, @RequestParam("page") int page,
      @RequestParam("pageSize") int pageSize) {
    List<GetVideoResponseDto> videos = videoService.getCategoryVideos(category, page, pageSize);
    return new ResponseEntity<List<GetVideoResponseDto>>(videos, HttpStatus.OK);
  }


  // 카테고리 이름 리스트 조회
  @ApiOperation(value = "카테고리 리스트 조회", notes = "카테고리 메뉴 등을 위한 카테고리 이름 추출")
  @GetMapping("/video/category")
  public ResponseEntity<List> getCategoryNames() {
    List categorys = videoService.getCategoryNames();
    return new ResponseEntity<List>(categorys, HttpStatus.OK);
  }


  // 조회수 급상승 TOP 10 + 디비 1시간 전 정보 저장
  @ApiOperation(value = "조회수 급상승 TOP 10", notes = "1시간마다 조회수가 급상승된 영상 10개 추출")
  @GetMapping("/video/top10")
  @Scheduled(cron = "0 0 0/1 * * *")
  public ResponseEntity<List<GetVideoResponseDto>> getViewsTop10Videos() {
    List<GetVideoResponseDto> videos = videoService.getViewsTop10Videos();
    return new ResponseEntity<List<GetVideoResponseDto>>(videos, HttpStatus.OK);
  }


  // 영상 상세 조회
  @ApiOperation(value = "영상 상세 조회 및 추천 영상 조회", notes = "영상 클릭시 상세 정보 + 추천 영상 추출")
  @GetMapping("/video/detail/{videoId}")
  public ResponseEntity<GetDetailResponseDto> getVideoDetail(@PathVariable("videoId") Long id,
      @CookieValue(name = "accessToken") String accessToken) {
    int viewsResult = videoService.plusViews(id); //  조회수 상승
    GetDetailResponseDto video = videoService.getVideoDetail(id);
    video.setIsFavorite(videoService.getFavorite(id, accessToken));// 좋아요 버튼 눌렀는지 여부
    return new ResponseEntity<GetDetailResponseDto>(video, HttpStatus.OK);
  }


  // 부모 댓글 전체 조회
  @ApiOperation(value = "부모 댓글 조회", notes = "부모 댓글 조회")
  @GetMapping("/comment/{videoId}/comment-parent")
  public List<CommentResponse> findParentComments(@PathVariable int videoId) {
    List<CommentResponse> list = commentService.findParentComments(videoId);

    return list;
  }


  // 자식 댓글 전체 조회
  @ApiOperation(value = "자식 댓글 조회", notes = "자식 댓글 조회")
  @GetMapping("/comment/{videoId}/comment-child")
  public List<CommentResponse> findChildComments(@PathVariable int videoId,
      @RequestParam int groupIndex) {
    List<CommentResponse> list = commentService.findChildComments(videoId, groupIndex);
    return list;
  }


  // 나의 채널 정보 조회
  @ApiOperation(value = "마이 채널 정보", notes = "나의 채널 정보")
  @GetMapping("/channel/{code}")
  public ResponseEntity<Channels> getMyChannelInfo(@PathVariable String code) {
    Accounts loginAccount = securityService.getSubjectAccount(code);
    System.out.println(channelService.findChannel(loginAccount.getAccountId()));
    return new ResponseEntity<Channels>(channelService.findChannel(loginAccount.getAccountId()),
        HttpStatus.OK);
  }


  // 인기 채널 top 10 조회
  @ApiOperation(value = "실시간 인기 채널 10", notes = "가장 인기 있는 채널 TOP10을 들고온다")
  @GetMapping("/channel/top10")
  public ResponseEntity<List<CustomChannelTop10>> getViewsTop10Channels() {
    List<CustomChannelTop10> channels = channelService.findLimitTimeSumCnt();
    channels.forEach(System.out::println);
    return new ResponseEntity<List<CustomChannelTop10>>(channels, HttpStatus.OK);
  }


  // 전체 채널 조회
  @ApiOperation(value = "전체 채널 조회", notes = "전체 채널에 대한 정보")
  @GetMapping("/channel")
  public ResponseEntity<List<Channels>> getAllChannels() {
    List<Channels> list = channelService.getAllChannels();
    list.forEach(System.out::println);
    return new ResponseEntity<List<Channels>>(list, HttpStatus.OK);
  }
}
