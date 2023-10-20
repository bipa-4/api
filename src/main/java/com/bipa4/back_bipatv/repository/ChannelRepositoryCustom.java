package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.channel.GetChannelDTO;
import com.bipa4.back_bipatv.dto.channel.GetChannelTop5DTO;
import com.bipa4.back_bipatv.dto.channel.GetSearchChannelDTO;
import com.bipa4.back_bipatv.dto.channel.SelectChannelDTO;
import java.util.List;
import java.util.UUID;

public interface ChannelRepositoryCustom {

  List<GetChannelDTO> getNotPrivateChannel(UUID page, int pageSize);

  List<GetChannelTop5DTO> findTop5Channels();

  SelectChannelDTO selectChannel(UUID channelId);

  UUID lastUUID();

  String getChannelNextUUID(UUID uuid);

  String getNextChannelVideoUUID(UUID videoId, UUID channelId, boolean flag);

  List<String> getSearchNextChannelVideoUUID(UUID videoId, UUID channelId, String searchQuery,
      boolean flag);

  List<UUID> lastUUIDSearchChannel(String searchQuery);

  List<GetSearchChannelDTO> getSearchChannel(UUID uuid, int pageSize, String searchQuery);
}
