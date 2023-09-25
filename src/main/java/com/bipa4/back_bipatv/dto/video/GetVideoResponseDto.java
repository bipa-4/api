package com.bipa4.back_bipatv.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@NoArgsConstructor
public class GetVideoResponseDto {

  private String channelName;
  private String channelProfileUrl;
  private String thumbnail;
  private String videoTitle;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+9")
  private Date createAt;
  private Integer readCnt;
  private Long videoId;

  public GetVideoResponseDto(String channelName, String channelProfileUrl, String thumbnail,
      String videoTitle, Date createAt, Integer readCnt, Long videoId) {
    this.channelName = channelName;
    this.channelProfileUrl = channelProfileUrl;
    this.thumbnail = thumbnail;
    this.videoTitle = videoTitle;
    this.createAt = createAt;
    this.readCnt = readCnt;
    this.videoId = videoId;
  }
}
