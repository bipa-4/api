package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.dto.video.GetVideoResponseDto;
import java.util.List;

public interface VideoRepositoryCustom {

  List<GetVideoResponseDto> getAllVideos(int page, int pageSize);
}
