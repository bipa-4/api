package com.bipa4.back_bipatv.entity;

import java.sql.Timestamp;
import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Comments")
public class Comments {

  @Id
  @GeneratedValue
  @Column(name = "comment_id")
  private int commentId;
  @Column(name = "content", length = 200, nullable = true)
  private String content;
  @Column(name = "parent_child", nullable = false)
  private int parentChild;
  @Column(name = "sort", nullable = false)
  private int sort;
  @Column(name = "group_index", nullable = false)
  private int groupIndex;
  @Column(name = "create_at", nullable = false)
  private Timestamp createAt;

  @ManyToOne
  @JoinColumn(name = "video_id")
  private Videos videos;
  @ManyToOne
  @JoinColumn(name = "account_id")
  private Accounts accounts;

}
