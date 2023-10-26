package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetCategoryNameRequestDto;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetSearchVideoINChannelDTO;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.dto.video.PutUpdateRequestDto;
import com.bipa4.back_bipatv.entity.Accounts;
import java.util.List;
import java.util.UUID;

public interface VideoRepositoryCustom {

  List<GetVideoResponseDto> getAllVideos(UUID page, int pageSize);

  UUID lastUUID();

  UUID getNextUUID(UUID uuid);

  UUID getNextCategoryUUID(UUID uuid, UUID category);

  UUID lastCategoryUUID(UUID category);

  List<GetVideoResponseDto> findByCategory(UUID category, UUID page, int pageSize);

  List<GetVideoResponseDto> findByViews();

  boolean updateViews();

  GetDetailResponseDto getDetail(UUID id);

  boolean remove(UUID id, Accounts account);

  boolean insert(PostUploadRequestDto videoResponseDto, Accounts account, UUID uuid);

  boolean update(UUID id, PutUpdateRequestDto videoResponseDto, Accounts account);

  List<GetCategoryNameRequestDto> getCategoryNames();

  boolean checkOwner(Accounts account, UUID videoId);

  boolean plusViews(UUID videoId);

  boolean getFavorite(UUID videoId, Accounts account);

  boolean plusLike(UUID videoId, Accounts account);

  boolean minusLike(UUID videoId, Accounts account);

  List<GetVideoResponseDto> getVideosInChannel(UUID channelId, UUID uuid, int pageSize);

  List<GetVideoResponseDto> getVideosInMyChannel(UUID channelId, UUID uuid, int pageSize);

  List<GetSearchVideoINChannelDTO> getSearchVideoInMyChannel(UUID channelId, Integer currentPage,
      int pageSize,
      String searchQuery);

  Integer lastUUIDSearchVideoInChannel(UUID channelId, String searchQuery);

  List<GetSearchVideoINChannelDTO> getSearchVideoInChannel(UUID channelId, Integer currentPage,
      int pageSize,
      String searchQuery);

  UUID lastUUIDInChannel(UUID channelId);

  UUID lastUUIDInMyChannel(UUID channelId);

  Integer lastUUIDSearchVideoInMyChannel(UUID channelId, String searchQuery);
}
