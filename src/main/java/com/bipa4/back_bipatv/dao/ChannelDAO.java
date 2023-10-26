package com.bipa4.back_bipatv.dao;

import com.bipa4.back_bipatv.dto.channel.GetChannelTop5DTO;
import com.bipa4.back_bipatv.entity.Channels;
import com.bipa4.back_bipatv.repository.ChannelRepository;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChannelDAO {


  private final ChannelRepository channelRepository;

  @Transactional
  public void createChannel(Channels channels) {
    channelRepository.save(channels);
  }

  @Transactional
  public Channels findChannel(Long accountId) {
    return channelRepository.findByChannelToAccountId(accountId).get();
  }

  @Transactional
  public List<GetChannelTop5DTO> findTop5Channels() {
    return channelRepository.findTop5Channels();
  }

  public UUID findByChannelId(UUID channelId) {
    return channelRepository.findByChannelId(channelId).getAccounts().getAccountId();
  }
}
