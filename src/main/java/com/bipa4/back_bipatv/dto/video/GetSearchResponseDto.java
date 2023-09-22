package com.bipa4.back_bipatv.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public interface GetSearchResponseDto {

  Long getVideo_id();

  String getThumbnail();

  String getTitle();

  String getContent();

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+9")
  Date getCreate_at();

  Integer getRead_cnt();

  Boolean getPrivate_type();

  String getProfile_url();

  String getName();
}
