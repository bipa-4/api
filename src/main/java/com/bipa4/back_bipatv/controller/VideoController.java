package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.PostSaveRequestDto;
import com.bipa4.back_bipatv.service.VideoService;
import java.io.IOException;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
@RequestMapping("/video")
public class VideoController {

  private final VideoService videoService;


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
