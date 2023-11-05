package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelRepositoryCustom {

    List<GetChannelDTO> getNotPrivateChannel(UUID page, int pageSize);

    List<GetChannelTop5DTO> findTop5Channels();

    SelectChannelDTO selectChannel(UUID channelId);

    UUID lastUUID();

    UUID getChannelNextUUID(UUID uuid);

    UUID getNextChannelVideoUUID(UUID videoId, UUID channelId, boolean flag);

    Integer getSearchNextChannelVideoRank(Integer rank, UUID channelId,
                                          String searchQuery, int pageSize);

    Integer getSearchNextMyChannelVideoRank(Integer rank, UUID channelId,
                                            String searchQuery, int pageSize);

    Integer lastUUIDSearchChannel(String searchQuery);

    List<GetSearchChannelDTO> getSearchChannel(Integer uuid, int pageSize, String searchQuery);

    Integer getNextChannelRank(String searchQuery, Integer ranking, int pageSize, Integer page);

    List<GetSumCommentNumGroupChannelDTO> getSumCommentNumGroupChannel();

    List<GetSumVideoViewNumGroupChannelDTO> getSumVideoViewNumGroupChannel();
    
}
