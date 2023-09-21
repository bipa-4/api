package com.bipa4.back_bipatv.dto.video;

public class GetUrlResponseDto {

  private String videoUrl;
  private String imageUrl;

  public GetUrlResponseDto(String videoUrl, String imageUrl) {
    this.videoUrl = videoUrl;
    this.imageUrl = imageUrl;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
