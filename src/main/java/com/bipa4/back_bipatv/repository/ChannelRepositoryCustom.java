package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.channel.GetChannelDTO;
import com.bipa4.back_bipatv.dto.channel.GetChannelTop5DTO;
import com.bipa4.back_bipatv.dto.channel.SelectChannelDTO;
import java.util.List;

public interface ChannelRepositoryCustom {

  List<GetChannelDTO> getNotPrivateChannel();

  List<GetChannelTop5DTO> findTop5Channels();

  SelectChannelDTO selectChannel(Long channelId);
}
