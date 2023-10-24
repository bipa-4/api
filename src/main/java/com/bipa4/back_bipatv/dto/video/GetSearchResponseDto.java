package com.bipa4.back_bipatv.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;


public interface GetSearchResponseDto {

  UUID getVideoId();

  UUID getChannelId();

  String getThumbnail();

  String getVideoTitle();

  String getContent();

  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+9")
  LocalDateTime getCreateAt();

  Integer getReadCount();

  boolean getPrivateType();

  String getChannelProfileUrl();

  String getChannelName();
}
