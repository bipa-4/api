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

  UUID getChannelNextUUID(UUID uuid);

  UUID getNextChannelVideoUUID(UUID videoId, UUID channelId, boolean flag);

  List<Integer> getSearchNextChannelVideoRank(Integer rank, UUID channelId, String searchQuery,
      int pageSize);

  List<Integer> getSearchNextMyChannelVideoRank(Integer rank, UUID channelId, String searchQuery,
      int pageSize);

  List<Integer> lastUUIDSearchChannel(String searchQuery);

  List<GetSearchChannelDTO> getSearchChannel(Integer uuid, int pageSize, String searchQuery);

  List<Integer> getNextChannelRank(String searchQuery, Integer ranking, int pageSize, Integer page);
}
