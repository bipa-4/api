package com.bipa4.back_bipatv.dto.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetVideoResponseDto {

  private String channelName;
  private String channelProfileUrl;
  private String thumbnail;
  private String videoTitle;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+9")
  private Date createAt;
  private Integer readCount;
  private Long videoId;
}
