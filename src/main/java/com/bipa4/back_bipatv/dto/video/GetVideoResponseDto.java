package com.bipa4.back_bipatv.dto.video;

import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class GetVideoResponseDto {

  private String name;
  private String profileUrl;
  private String thumbnail;
  private String title;
  private Timestamp createAt;
  private Integer readCnt;
  private Long videoId;

  public GetVideoResponseDto(String name, String profileUrl, String thumbnail, String title,
      Timestamp createAt, Integer readCnt, Long videoId) {
    this.name = name;
    this.profileUrl = profileUrl;
    this.thumbnail = thumbnail;
    this.title = title;
    this.createAt = createAt;
    this.readCnt = readCnt;
    this.videoId = videoId;
  }
}
