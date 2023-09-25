package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetDetailResponseDto;
import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import com.bipa4.back_bipatv.dto.video.PostUploadRequestDto;
import com.bipa4.back_bipatv.entity.Channels;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

public interface VideoRepositoryCustom {

  List<GetVideoResponseDto> getAllVideos(int page, int pageSize);

  List<GetVideoResponseDto> findByCategory(String category, int page, int pageSize);

  List<GetVideoResponseDto> findByViews();

  @Transactional
  @Modifying
  int updateViews();

  List<GetDetailResponseDto> getDetail(Long id);

  @Transactional
  @Modifying
  Long remove(Long id, Channels channelId);

  @Transactional
  @Modifying
  int insert(PostUploadRequestDto videoResponseDto);

  List getCategoryNames();
}
