package com.bipa4.back_bipatv.dto.video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostUploadRequestDto {

  private String videoUrl;
  private String thumbnailUrl;
  private String title;
  private String content;
  private Boolean privateType;
  private int category;
}
