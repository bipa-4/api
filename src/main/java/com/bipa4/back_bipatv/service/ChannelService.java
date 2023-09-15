package com.bipa4.back_bipatv.service;

import com.bipa4.back_bipatv.dao.ChannelDAO;
import com.bipa4.back_bipatv.dto.channel.CustomChannelTop10;
import com.bipa4.back_bipatv.entity.Channels;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {

  @Autowired
  ChannelDAO channelDAO;

  public Channels findChannel(Long accountId) {
    return channelDAO.findChannel(accountId);
  }

  public List<CustomChannelTop10> findLimitTimeSumCnt() {
    return channelDAO.findLimitTimeSumCnt();
  }

  public List<Channels> getAllChannels() {
    return channelDAO.getAllChannels();
  }
}
