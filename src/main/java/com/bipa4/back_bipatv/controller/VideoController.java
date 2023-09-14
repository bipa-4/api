package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.video.GetAllResponseDto;
import com.bipa4.back_bipatv.dto.video.PostSaveRequestDto;
import com.bipa4.back_bipatv.service.VideoService;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
@RequestMapping("/streamwave")
public class VideoController {

  private final VideoService videoService;

  // 전체 조회 (최신 순으로)
  // TODO : 페이지네이션
  @GetMapping("/")
  public ResponseEntity<List<GetAllResponseDto>> getAllVideos() {
    List<GetAllResponseDto> videos = videoService.findAll();
    return new ResponseEntity<List<GetAllResponseDto>>(videos, HttpStatus.OK);
  }

  // 카테고리별 조회
  @GetMapping("/{category}")
  public ResponseEntity<List<GetAllResponseDto>> getCategoryVideos(
      @PathVariable("category") String category) {
    List<GetAllResponseDto> videos = videoService.findByCategory(category);
    return new ResponseEntity<List<GetAllResponseDto>>(videos, HttpStatus.OK);
  }

  // 조회수 급상승 TOP 10 + 디비 1시간 전 정보 저장
  // cron = "0 0 0/1 * * *"
  @GetMapping("/top10")
  @Scheduled(cron = "0 0 0/1 * * *")
  public ResponseEntity<List<GetAllResponseDto>> getViewsTop10Videos() {
    List<GetAllResponseDto> videos = videoService.findByViews();
    int result = videoService.updateViews();
    return new ResponseEntity<List<GetAllResponseDto>>(videos, HttpStatus.OK);
  }

  // 영상 업로드(S3) + 영상 정보 업로드(DB)
  @PostMapping("/upload")
  public ResponseEntity uploadVideo(@RequestPart(value = "dto") PostSaveRequestDto requestDto,
      @RequestParam(value = "file") MultipartFile multipartFile) {

    try {

      // Upload multipartFile to storage and return url.
      String url = videoService.uploadFile(multipartFile, String.valueOf(requestDto.getChannelId()),
          LocalTime.now() + " ");

      // Create a Dto to upload info to db.
      PostSaveRequestDto processedRequestDto = PostSaveRequestDto.builder()
          .title(requestDto.getTitle()).content(requestDto.getContent()).videoUrl(url)
          .privateType(requestDto.isPrivateType())
          .commentPermission(requestDto.isCommentPermission()).thumbnail(requestDto.getThumbnail())
          .channelId(requestDto.getChannelId()).build();

      // Upload to db.
      videoService.upload(processedRequestDto);
      return new ResponseEntity(HttpStatus.OK);
    } catch (IOException e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }
}
