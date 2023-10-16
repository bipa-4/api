package com.bipa4.back_bipatv.dto.user;

import com.bipa4.back_bipatv.dataType.ELogin_Type;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetAccountCheckDTO {

  private UUID accountId;
  private Timestamp joinDate;
  private ELogin_Type loginType;
  private String loginId;
  private String userName;
  private String userProfileUrl;
  private String eMail;
  private Timestamp deleteAt;
  private UUID channelId;
  private String channelProfileUrl;
}
