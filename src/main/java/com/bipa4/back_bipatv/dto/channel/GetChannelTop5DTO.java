package com.bipa4.back_bipatv.dto.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetChannelTop5DTO {

  private Integer ranking; // ranking 필드 추가
  private String channelName;
  private String profileUrl;
  private String content;
  private Integer timeLimitSumCnt;
}
