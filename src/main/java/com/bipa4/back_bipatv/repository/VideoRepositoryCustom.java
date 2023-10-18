package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetCategoryNameRequestDto;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.dto.video.PutUpdateRequestDto;
import com.bipa4.back_bipatv.entity.Accounts;
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

  Long remove(UUID id, Accounts account);

  int insert(PostUploadRequestDto videoResponseDto, String token, UUID uuid);

  int update(UUID id, PutUpdateRequestDto videoResponseDto, Accounts account);

  List<GetCategoryNameRequestDto> getCategoryNames();

  Long checkOwner(Accounts account, UUID videoId);

  int plusViews(UUID videoId);

  Long getFavorite(UUID videoId, String token);

  int plusLike(UUID videoId, String token);

  int minusLike(UUID videoId, String token);

  List<GetVideoResponseDto> getVideosInChannel(UUID channelId, UUID uuid, int pageSize);
}
