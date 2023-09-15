package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dto.channel.CustomChannelTop10;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.repository.ChannelRepository;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChannelDAO {


  @Autowired
  private ChannelRepository channelRepository;

  @Transactional
  public void createChannel(Channels channels) {
    channelRepository.save(channels);
  }

  public Channels findChannel(Long accountId) {
    return channelRepository.findByChannelName(accountId).get();
  }

  public List<CustomChannelTop10> findLimitTimeSumCnt() {
    return channelRepository.findLimitTimeSumCnt();
  }

  public List<Channels> getAllChannels() {
    return channelRepository.findAll();
  }
}
