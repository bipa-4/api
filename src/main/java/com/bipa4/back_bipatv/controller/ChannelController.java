package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.channel.CustomChannelTop10;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.service.ChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"ChannelController"})
@RequestMapping("/channel")
@RestController
public class ChannelController {

  @Autowired
  ChannelService channelService;

  @ApiOperation(value = "마이 채널 정보", notes = "나의 채널 정보")
  @GetMapping("/{accountID}")
  public ResponseEntity<Channels> getMyChannelInfo(@PathVariable Long accountID) {
    System.out.println(channelService.findChannel(accountID));
    return new ResponseEntity<Channels>(channelService.findChannel(accountID), HttpStatus.OK);
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
}
