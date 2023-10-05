package com.bipa4.back_bipatv.dto.channel;

import com.bipa4.back_bipatv.entity.Accounts;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PutChannelDTO {

  private String content;
  private String channelName;
  private boolean privateType;
  private String profileUrl;
  private Accounts accounts;

}
