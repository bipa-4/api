package com.bipa4.back_bipatv.dto.comment;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

  private UUID commentId;
  private String content;
  private int parentChild;
  private int groupIndex;
  private Timestamp createAt;
  private UUID videoId;
  private UUID accountId;

  @Override
  public String toString() {
    return "CommentRequest{" +
        "commentId=" + commentId +
        ", content='" + content + '\'' +
        ", parentChild=" + parentChild +
        ", groupIndex=" + groupIndex +
        ", createAt=" + createAt +
        ", videoId=" + videoId +
        ", accountId=" + accountId +
        '}';
  }

}
