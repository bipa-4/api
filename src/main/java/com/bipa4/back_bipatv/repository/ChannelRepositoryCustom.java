package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.channel.GetChannelDTO;
import java.util.List;

public interface ChannelRepositoryCustom {

  List<GetChannelDTO> getNotPrivateChannel();

}
