package com.bipa4.back_bipatv.entity;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Table(name = "Comments")
public class Comments {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "comment_id", columnDefinition = "BINARY(16)")
  private UUID commentId;
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
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "video_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Videos videos;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Accounts accounts;

  public String getComment_id() {
    return commentId != null ? commentId.toString() : null;
  }

}
