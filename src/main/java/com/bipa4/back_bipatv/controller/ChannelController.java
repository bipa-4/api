package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelController {

  @Autowired
  ChannelService channelService;

  @GetMapping("/channel/{accountID}")
  public ResponseEntity<Channels> getMyChannelInfo(@PathVariable Long accountID) {
    System.out.println(channelService.findChannel(accountID));
    return new ResponseEntity<Channels>(channelService.findChannel(accountID), HttpStatus.OK);
  }
}
