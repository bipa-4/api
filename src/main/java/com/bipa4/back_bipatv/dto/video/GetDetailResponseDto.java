package com.bipa4.back_bipatv.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@Setter
@Getter
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
  private String thumbnailUrl;
  private List<GetVideoResponseDto> recommendedList;
  private Long favoriteCnt;
}
