package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.ChannelDAO;
import com.bipa4.back_bipatv.dto.channel.CustomChannelTop10;
import com.bipa4.back_bipatv.entity.Accounts;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.exception.ResourceNotFoundException;
import com.bipa4.back_bipatv.repository.ChannelRepository;
import com.bipa4.back_bipatv.security.SecurityService;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {

  @Autowired
  ChannelDAO channelDAO;
  @Autowired
  SecurityService securityService;
  @Autowired
  ChannelRepository channelRepository;

  public Channels findChannel(Long accountId) {
    return channelDAO.findChannel(accountId);
  }

  public List<CustomChannelTop10> findLimitTimeSumCnt() {
    return channelDAO.findLimitTimeSumCnt();
  }

  public List<Channels> getAllChannels() {
    return channelDAO.getAllChannels();
  }

  @Transactional
  public Channels updateChannel(String code, Channels channel) {
    Accounts loginAccount = securityService.getSubjectAccount(code);
    if (loginAccount != null) {
      Channels myChannel = findChannel(loginAccount.getAccountId());
      if (!myChannel.getContent().equals(channel.getContent())) {
        myChannel.setContent(channel.getContent());
      }
      if (!myChannel.getProfileUrl().equals(channel.getProfileUrl())) {
        myChannel.setProfileUrl(channel.getProfileUrl());
      }
      if (!(myChannel.isPrivateType() == channel.isPrivateType())) {
        myChannel.setPrivateType(channel.isPrivateType());
      }
      return channelRepository.save(myChannel);
    }
    throw new ResourceNotFoundException(
        "User Channel not found");
  }
}
