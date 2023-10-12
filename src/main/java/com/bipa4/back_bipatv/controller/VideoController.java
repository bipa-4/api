package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.video.GetCDNUrlResponseDto;
import com.bipa4.back_bipatv.dto.video.GetSearchResponseDto;
import com.bipa4.back_bipatv.dto.video.GetUrlResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.dto.video.PutUpdateRequestDto;
import com.bipa4.back_bipatv.service.PresignedUrlService;
import com.bipa4.back_bipatv.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = {"VideoController"})
@RequestMapping("/video")
@RequiredArgsConstructor
@Controller
public class VideoController {

  private final VideoService videoService;
  private final PresignedUrlService presignedUrlService;

  // 본인 영상인지 확인
  @ApiOperation(value = "본인의 영상이 맞는지 확인", notes = "토큰을 통해 본인의 영상이 맞는지 확인 (삭제 또는 업로드 등애 사용)")
  @GetMapping("/check")
  public ResponseEntity<Boolean> checkVideos(@CookieValue(name = "accessToken") String accessToken,
      @RequestParam("videoId") UUID videoId) {
    Long owner = videoService.check(accessToken, videoId);
    if (owner > 0) {
      return new ResponseEntity<>(true, HttpStatus.OK);
    }
    return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
  }


  // 영상  삭제
  @ApiOperation(value = "영상 삭제", notes = "영상 삭제 진행")
  @DeleteMapping("/{id}")
  public ResponseEntity<String> ResponseEntity(@PathVariable("id") Long id) {

    return videoService.removeVideo(id) == 1 ? new ResponseEntity<>("success",
        HttpStatus.OK)
        : new ResponseEntity<>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
  }


  // S3 presigned-url 발급
  @ApiOperation(value = "S3 presigned-url 발급", notes = "비디오 및 이미지 업로드를 위한 임시 url 발급")
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


  @ApiOperation(value = "CDN presigned-url 발급", notes = "비디오 및 이미지 업로드를 위한 임시 url 발급")
  @PostMapping("/presigned-cdn")
  public ResponseEntity<GetCDNUrlResponseDto> saveFileCDN(
      @RequestParam("videoName") String videoName,
      @RequestParam("imageName") String imageName) {
    return new ResponseEntity<>(presignedUrlService.getPreSignedUrlCDN(videoName, imageName),
        HttpStatus.OK);
  }

  // 영상 업로드
  @ApiOperation(value = "영상 업로드", notes = "영상 업로드 진행")
  @PostMapping("/upload")
  public ResponseEntity<Long> upload(
      @RequestBody @ApiParam(value = "수정할 회원 정보", required = true) PostUploadRequestDto responseDto,
      @CookieValue(name = "accessToken") String accessToken) {
    System.out.println(accessToken);
    if (videoService.uploadVideo(responseDto, accessToken) == 0) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }


  // 영상 수정
  @ApiOperation(value = "영상 수정", notes = "영상 수정 진행")
  @PutMapping("/{id}")
  public ResponseEntity<Long> update(@PathVariable Long id,
      @RequestBody PutUpdateRequestDto requestDto) {
    if (videoService.updateVideo(id, requestDto) == 0) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }


  // 영상 검색
  @ApiOperation(value = "영상 검색", notes = "Full-text searches 진행")
  @GetMapping("/search")
  public ResponseEntity<List<GetSearchResponseDto>> search(
      @RequestParam("search_query") String searchQuery) {
    return new ResponseEntity<List<GetSearchResponseDto>>(
        videoService.search(searchQuery), HttpStatus.OK);
  }


  // 좋아요
  @ApiOperation(value = "좋아요", notes = "줗아요 버튼을 눌렀을 시")
  @GetMapping("/detail/{videoId}/like")
  public ResponseEntity<Boolean> like(@PathVariable("videoId") UUID videoId,
      @CookieValue(name = "accessToken") String accessToken) {
    return videoService.like(videoId, accessToken) == 1 ? new ResponseEntity<>(true,
        HttpStatus.OK)
        : new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
  }


  // 좋아요 취소
  @ApiOperation(value = "좋아요 취소", notes = "줗아요 버튼을 다시 눌렀을 시")
  @DeleteMapping("/detail/{videoId}/like")
  public ResponseEntity<Boolean> cancelLike(
      @ApiParam(value = "좋아요를 취소할 영상 아이디") @PathVariable("videoId") UUID videoId,
      @ApiParam(value = "좋아요를 취소할 유저의 토큰값") @CookieValue(name = "accessToken") String accessTokenn) {
    return videoService.cancelLike(videoId, accessTokenn) == 1 ? new ResponseEntity<>(true,
        HttpStatus.OK)
        : new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 영상 업로드(S3) + 영상 정보 업로드(DB) [기존 방식 - 백엔드에서 영상 올리기]
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
