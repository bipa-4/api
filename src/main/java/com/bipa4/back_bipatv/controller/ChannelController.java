package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.channel.CustomChannelTop10;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.exception.ResourceNotFoundException;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.ChannelService;
import com.bipa4.back_bipatv.service.PresignedUrlService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"ChannelController"})
@RequestMapping("/channel")
@RestController
public class ChannelController {

  @Autowired
  private ChannelService channelService;
  @Autowired
  private SecurityService securityService;
  @Autowired
  private PresignedUrlService presignedUrlService;

  @ApiOperation(value = "마이 채널 정보", notes = "나의 채널 정보")
  @GetMapping("/{code}")
  public ResponseEntity<Channels> getMyChannelInfo(@PathVariable String code) {
    Accounts loginAccount = securityService.getSubjectAccount(code);
    System.out.println(channelService.findChannel(loginAccount.getAccountId()));
    return new ResponseEntity<Channels>(channelService.findChannel(loginAccount.getAccountId()),
        HttpStatus.OK);
  }

  @ApiOperation(value = "실시간 인기 채널 10", notes = "가장 인기 있는 채널 TOP10을 들고온다")
  @GetMapping("/top10")
  public ResponseEntity<List<CustomChannelTop10>> getViewsTop10Videos() {
    List<CustomChannelTop10> channels = channelService.findLimitTimeSumCnt();
    channels.forEach(System.out::println);
    return new ResponseEntity<List<CustomChannelTop10>>(channels, HttpStatus.OK);
  }

  @ApiOperation(value = "전체 채널 조회", notes = "전체 채널에 대한 정보")
  @GetMapping("")
  public ResponseEntity<List<Channels>> getAllChannels() {
    List<Channels> list = channelService.getAllChannels();
    list.forEach(System.out::println);
    return new ResponseEntity<List<Channels>>(list, HttpStatus.OK);
  }

  @ApiOperation(value = "updateMyChannel", notes = "채널 정보 수정")
  @PutMapping("/{code}")
  public ResponseEntity<Channels> updateMyChannelInfo(@PathVariable String code,
      @RequestBody Channels channel) {
    try {
      Channels updatedChannel = channelService.updateChannel(code, channel);
      return new ResponseEntity<>(updatedChannel, HttpStatus.OK);
    } catch (ResourceNotFoundException e) {
      // 리소스를 찾지 못한 경우 404 에러를 반환
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

  }

  @ApiOperation(value = "I Want S3 URI", notes = "S3 URI요청")
  @PostMapping("/presigned")
  public ResponseEntity<String> saveFile(@RequestParam("imageName") String imageName) {
    String url = presignedUrlService.getPreSignedUrl(imageName);
    if (url != null) {
      return new ResponseEntity<>(url, HttpStatus.OK);
    }
    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
  }
}
