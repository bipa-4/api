package com.bipa4.back_bipatv.dto.channel;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Data
public class GetSumCommentNumGroupChannelDTO {

  private UUID channelId;
  private int sumCommentNum;
}
