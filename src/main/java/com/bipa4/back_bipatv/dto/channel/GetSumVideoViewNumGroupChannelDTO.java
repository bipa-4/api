package com.bipa4.back_bipatv.dto.channel;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class GetSumVideoViewNumGroupChannelDTO {

  private UUID channelId;
  private int sumViewLogNum;
}
