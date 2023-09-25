package com.bipa4.back_bipatv.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@NoArgsConstructor
public class GetDetailResponseDto {

  private String channelName;
  private String channelProfileUrl;
  private String videoUrl;
  private String videoTitle;
  private String content;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+9")
  private Timestamp createAt;
  private Integer readCnt;
  private Long videoId;

  public GetDetailResponseDto(String channelName, String channelProfileUrl, String videoUrl,
      String videoTitle, String content, Timestamp createAt, Integer readCnt, Long videoId) {
    this.channelName = channelName;
    this.channelProfileUrl = channelProfileUrl;
    this.videoUrl = videoUrl;
    this.videoTitle = videoTitle;
    this.content = content;
    this.createAt = createAt;
    this.readCnt = readCnt;
    this.videoId = videoId;
  }
}
