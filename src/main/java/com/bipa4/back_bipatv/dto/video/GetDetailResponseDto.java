package com.bipa4.back_bipatv.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@NoArgsConstructor
public class GetDetailResponseDto {

  private String channelName;
  private String channelProfileUrl;
  private Long channelId;
  private String videoUrl;
  private String videoTitle;
  private String content;
  private Boolean isFavorite;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+9")
  private Timestamp createAt;
  private Integer readCnt;
  private Long videoId;
  private List<GetVideoResponseDto> recommendedList;

  public GetDetailResponseDto(String channelName, String channelProfileUrl, Long channelId,
      String videoUrl, String videoTitle, String content, Boolean isFavorite, Timestamp createAt,
      Integer readCnt, Long videoId, List<GetVideoResponseDto> recommendedList) {
    this.channelName = channelName;
    this.channelProfileUrl = channelProfileUrl;
    this.channelId = channelId;
    this.videoUrl = videoUrl;
    this.videoTitle = videoTitle;
    this.content = content;
    this.isFavorite = isFavorite;
    this.createAt = createAt;
    this.readCnt = readCnt;
    this.videoId = videoId;
    this.recommendedList = recommendedList;
  }
}
