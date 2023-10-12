package com.bipa4.back_bipatv.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;


public interface GetSearchResponseDto {

  String getVideoId();

  String getThumbnail();

  String getVideoTitle();

  String getContent();

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+9")
  Date getCreateAt();

  Integer getReadCnt();

  Boolean getPrivateType();

  String getChannelProfileUrl();

  String getChannelName();
}
