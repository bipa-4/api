package com.bipa4.back_bipatv.dto.video;

import java.time.LocalDateTime;

public interface GetAllResponseDto {

  String getName();

  String getProfile_url();

  String getThumbnail();

  String getTitle();

  LocalDateTime getCreate_at();

  Integer getRead_cnt();

  Long getVideo_id();
}
