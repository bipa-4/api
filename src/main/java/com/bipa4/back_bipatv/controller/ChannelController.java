package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.channel.PutChannelDTO;
import com.bipa4.back_bipatv.dto.video.GetImageUrlResponseDto;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.exception.ResourceNotFoundException;
import com.bipa4.back_bipatv.service.ChannelService;
import com.bipa4.back_bipatv.service.PresignedUrlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"ChannelController"})
@RequestMapping("/channel")
@RequiredArgsConstructor
@RestController
public class ChannelController {

  private final ChannelService channelService;
  private final PresignedUrlService presignedUrlService;


  @ApiOperation(value = "updateMyChannel", notes = "채널 정보 수정")
  @PutMapping("/{channelId}")
  public ResponseEntity<Channels> updateMyChannelInfo(@PathVariable UUID chnnaelId,
      @CookieValue(value = "accessToken", required = false) String code,
      @RequestBody PutChannelDTO putChannelDTO) {
    try {
      Channels updatedChannel = channelService.updateChannel(chnnaelId, code, putChannelDTO);
      return new ResponseEntity<>(updatedChannel, HttpStatus.OK);
    } catch (ResourceNotFoundException e) {
      // 리소스를 찾지 못한 경우 404 에러를 반환
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @ApiOperation(value = "I Want S3 URI", notes = "S3 URI요청")
  @PostMapping("/presigned")
  public ResponseEntity<GetImageUrlResponseDto> saveFile(
      @RequestParam("imageName") String imageName) {
    GetImageUrlResponseDto responseDto = presignedUrlService.getPreSignedUrl(imageName);
    if (responseDto != null) {
      return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
  }


}
