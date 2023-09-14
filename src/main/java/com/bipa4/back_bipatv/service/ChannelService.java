package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.ChannelDAO;
import com.bipa4.back_bipatv.entity.Channels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {

  @Autowired
  ChannelDAO channelDAO;

  public Channels findChannel(Long accountId) {
    return channelDAO.findChannel(accountId);
  }

}
