package com.bipa4.back_bipatv.dto.video;

import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class GetDetailResponseDto {

  private String name;
  private String profileUrl;
  private String videoUrl;
  private Boolean commentPermission;
  private String title;
  private String content;
  private Timestamp createAt;
  private Integer readCnt;
  private Long videoId;

  public GetDetailResponseDto(String name, String profileUrl, String videoUrl,
      Boolean commentPermission, String title, String content, Timestamp createAt, Integer readCnt,
      Long videoId) {
    this.name = name;
    this.profileUrl = profileUrl;
    this.videoUrl = videoUrl;
    this.commentPermission = commentPermission;
    this.title = title;
    this.content = content;
    this.createAt = createAt;
    this.readCnt = readCnt;
    this.videoId = videoId;
  }
}
