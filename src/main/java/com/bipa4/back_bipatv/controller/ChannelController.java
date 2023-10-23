package com.bipa4.back_bipatv.controller;

import com.bipa4.back_bipatv.dto.channel.PutChannelDTO;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.security.SecurityService;
import com.bipa4.back_bipatv.service.ChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"ChannelController"})
@RequestMapping("/channel")
@RequiredArgsConstructor
@RestController
public class ChannelController {

  private final ChannelService channelService;
  private final SecurityService securityService;


  @ApiOperation(value = "updateMyChannel", notes = "채널 정보 수정")
  @PutMapping("/{channelId}")
  public ResponseEntity<Channels> updateMyChannelInfo(@PathVariable UUID channelId,
      @CookieValue(value = "accessToken", required = false) String code,
      @RequestBody PutChannelDTO putChannelDTO) {
    Channels updatedChannel = channelService.updateChannel(channelId, code, putChannelDTO);
    return new ResponseEntity<>(updatedChannel, HttpStatus.OK);
  }

  @ApiOperation(value = "getUpdateFlag", notes = "업데이트 플레그 얻기")
  @GetMapping("/flag/{channelId}")
  public ResponseEntity<Boolean> getUpdateFlag(
      @CookieValue(value = "accessToken", required = false) String accessToken,
      @PathVariable("channelId") UUID channelId) {
    Accounts loginAccount = securityService.getSubjectAccount(accessToken);
    return new ResponseEntity<>(channelService.getUpdateFlag(loginAccount, channelId),
        HttpStatus.OK);
  }
}
