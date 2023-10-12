package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetCategoryNameRequestDto;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.dto.video.PutUpdateRequestDto;
import java.util.List;
import java.util.UUID;

public interface VideoRepositoryCustom {

  List<GetVideoResponseDto> getAllVideos(UUID page, int pageSize);

  UUID lastUUID();

  UUID lastUUIDInChannel(UUID channelId);

  UUID lastCategoryUUID(UUID category);

  String getNextUUID(UUID uuid);

  List<GetVideoResponseDto> findByCategory(UUID category, UUID page, int pageSize);

  List<GetVideoResponseDto> findByViews();

  int updateViews();

  GetDetailResponseDto getDetail(UUID id);

  Long remove(Long id);

  int insert(PostUploadRequestDto videoResponseDto, String token, UUID uuid);

  int update(Long id, PutUpdateRequestDto videoResponseDto);

  List<GetCategoryNameRequestDto> getCategoryNames();

  Long checkOwner(String token, UUID videoId);

  int plusViews(UUID videoId);

  Long getFavorite(UUID videoId, String token);

  int plusLike(UUID videoId, String token);

  int minusLike(UUID videoId, String token);

  List<GetVideoResponseDto> getVideosInChannel(UUID channelId, UUID uuid, int pageSize);
}
