package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetCategoryNameRequestDto;
import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.dto.video.PutUpdateRequestDto;
import java.util.List;

public interface VideoRepositoryCustom {

  List<GetVideoResponseDto> getAllVideos(int page, int pageSize);

  List<GetVideoResponseDto> findByCategory(String category, int page, int pageSize);

  List<GetVideoResponseDto> findByViews();

  int updateViews();

  GetDetailResponseDto getDetail(Long id);

  Long remove(Long id);

  int insert(PostUploadRequestDto videoResponseDto, String token);

  int update(Long id, PutUpdateRequestDto videoResponseDto);

  List<GetCategoryNameRequestDto> getCategoryNames();

  Long checkOwner(String token, Long videoId);

  int plusViews(Long videoId);

  Long getFavorite(Long videoId, String token);

  int plusLike(Long videoId, String token);

  int minusLike(Long videoId, String token);

  List<GetVideoResponseDto> getVideosInChannel(Long channelId);
}
