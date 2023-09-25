package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetSearchResponseDto;
import com.bipa4.back_bipatv.dto.video.GetUrlResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.repository.VideoRepository;
import com.bipa4.back_bipatv.service.PresignedUrlService;
import com.bipa4.back_bipatv.service.VideoService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.transaction.Transactional;
import javax.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = {"VideoController"})
@RequestMapping("/video")
@RequiredArgsConstructor
@Controller
public class VideoController {

  private final VideoService videoService;
  private final VideoRepository videoRepository;
  private final PresignedUrlService presignedUrlService;


  // 전체 조회 (최신 순으로)
  @GetMapping("/latest")
  public ResponseEntity<List<GetVideoResponseDto>> getAllVideos(@PathParam("page") int page,
      @PathParam("pageSize") int pageSize) {
    List<GetVideoResponseDto> videos = videoRepository.getAllVideos(page, pageSize);
    return new ResponseEntity<List<GetVideoResponseDto>>(videos, HttpStatus.OK);
  }


  // 카테고리별 조회
  @GetMapping("/category/{category}")
  public ResponseEntity<List<GetVideoResponseDto>> getCategoryVideos(
      @PathVariable("category") String category, @PathParam("page") int page,
      @PathParam("pageSize") int pageSize) {
    System.out.println(category);
    List<GetVideoResponseDto> videos = videoRepository.findByCategory(category, page, pageSize);
    return new ResponseEntity<List<GetVideoResponseDto>>(videos, HttpStatus.OK);
  }


  // 카테고리 이름 리스트 조회
  @GetMapping("/category")
  public ResponseEntity<List> getCategoryVideos() {
    List categorys = videoRepository.getCategoryNames();
    return new ResponseEntity<List>(categorys, HttpStatus.OK);
  }


  // 조회수 급상승 TOP 10 + 디비 1시간 전 정보 저장
  @GetMapping("/top10")
  @Scheduled(cron = "0 0 0/1 * * *")
  public ResponseEntity<List<GetVideoResponseDto>> getViewsTop10Videos() {
    List<GetVideoResponseDto> videos = videoRepository.findByViews();
    int result = videoRepository.updateViews();
    return new ResponseEntity<List<GetVideoResponseDto>>(videos, HttpStatus.OK);
  }


  // 영상 상세 조회
  @GetMapping("/detail/{id}")
  public ResponseEntity<List<GetDetailResponseDto>> getVideoDetail(@PathVariable("id") Long id) {
    List<GetDetailResponseDto> video = videoRepository.getDetail(id);
    return new ResponseEntity<List<GetDetailResponseDto>>(video, HttpStatus.OK);
  }


  // 영상  삭제
  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<String> ResponseEntity(@PathVariable("id") Long id,
      @RequestParam("channelId") Long channelId) {

    Channels channel = new Channels();
    channel.setChannelId(channelId);

    return videoRepository.remove(id, channel) == 1 ? new ResponseEntity<>("댓글 삭제 성공",
        HttpStatus.OK)
        : new ResponseEntity<>("비디오 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR);
  }


  // S3 presigned-url 발급
  @PostMapping("/presigned")
  public ResponseEntity<GetUrlResponseDto> saveFile(@RequestParam("videoName") String videoName,
      @RequestParam("imageName") String imageName) {
    GetUrlResponseDto urls = new GetUrlResponseDto(presignedUrlService.getPreSignedUrl(videoName),
        presignedUrlService.getPreSignedUrl(imageName));

    if (urls.getVideoUrl() == null || urls.getVideoUrl() == null) {
      return new ResponseEntity<>(urls, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(urls, HttpStatus.OK);
  }


  // 영상 업로드
  @PostMapping("/upload")
  public ResponseEntity<Long> upload(@RequestBody PostUploadRequestDto responseDto) {
    if (videoRepository.insert(responseDto) == 0) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }


  // 영상 검색
  @GetMapping("/search")
  public ResponseEntity<List<GetSearchResponseDto>> search(
      @RequestParam("search_query") String searchQuery) {
    return new ResponseEntity<List<GetSearchResponseDto>>(
        videoService.search(searchQuery), HttpStatus.OK);
  }

  // 영상 업로드(S3) + 영상 정보 업로드(DB) [기존 방식 - 백엔드에서 영상 올리기] 삭제 금지 !!!!!!
//  @PostMapping("/upload")
//  public ResponseEntity uploadVideo(@RequestPart(value = "dto") PostSaveRequestDto requestDto,
//      @RequestParam(value = "file") MultipartFile multipartFile) {
//
//    try {
//
//      // Upload multipartFile to storage and return url.
//      String url = videoService.uploadFile(multipartFile, String.valueOf(requestDto.getChannelId()),
//          LocalTime.now() + " ");
//
//      // Create a Dto to upload info to db.
//      PostSaveRequestDto processedRequestDto = PostSaveRequestDto.builder()
//          .title(requestDto.getTitle()).content(requestDto.getContent()).videoUrl(url)
//          .privateType(requestDto.isPrivateType())
//          .commentPermission(requestDto.isCommentPermission()).thumbnail(requestDto.getThumbnail())
//          .channelId(requestDto.getChannelId()).build();
//
//      // Upload to db.
//      videoService.upload(processedRequestDto);
//      return new ResponseEntity(HttpStatus.OK);
//    } catch (IOException e) {
//      return new ResponseEntity(HttpStatus.BAD_REQUEST);
//    }
//  }
}
