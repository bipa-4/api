package com.bipa4.back_bipatv.dto.video;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PutUpdateRequestDto {

  private String videoUrl;
  private String thumbnailUrl;
  private String title;
  private String content;
  private Boolean private_type;

  public PutUpdateRequestDto(String videoUrl, String thumbnailUrl, String title,
      String content, Boolean private_type) {
    this.videoUrl = videoUrl;
    this.thumbnailUrl = thumbnailUrl;
    this.title = title;
    this.content = content;
    this.private_type = private_type;
  }
}
