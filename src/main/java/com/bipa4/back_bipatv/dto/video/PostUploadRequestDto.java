package com.bipa4.back_bipatv.dto.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUploadRequestDto {

  private String videoUrl;
  private String thumbnailUrl;
  private String title;
  private String content;
  private Boolean private_type;
  private String userToken;

  @Override
  public String toString() {
    return "PostUploadRequestDto{" +
        "videoUrl='" + videoUrl + '\'' +
        ", thumbnailUrl='" + thumbnailUrl + '\'' +
        ", title='" + title + '\'' +
        ", content='" + content + '\'' +
        ", private_type=" + private_type +
        ", userToken='" + userToken + '\'' +
        '}';
  }
}
