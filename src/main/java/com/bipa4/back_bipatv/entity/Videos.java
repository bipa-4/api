package com.bipa4.back_bipatv.entity;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

@Entity
@Data
@Table(name = "Videos")
@NoArgsConstructor
public class Videos {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(columnDefinition = "BINARY(16)")
  @Type(type = "uuid-binary")
  private UUID videoId;
  @Column(name = "video_url", nullable = true, length = 200)
  private String videoUrl;
  @Column(name = "title", nullable = true, length = 100)
  private String title;
  @Column(name = "content", nullable = true, length = 400)
  private String content;
  @Column(name = "read_cnt", nullable = true)
  private int readCnt;
  @Column(name = "create_at", nullable = true)
  private Timestamp createAt;
  @Column(name = "private_type")
  private boolean privateType;
  @Column(name = "comment_permission")
  private boolean commentPermission;
  @Column(name = "thumbnail", nullable = true, length = 200)
  private String thumbnail;
  @ManyToOne
  @JoinColumn(name = "channel_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Channels channelId;

//  @Builder
//  public Videos(String videoUrl, String title, String content, boolean privateType,
//      boolean commentPermission, String thumbnail, Channels channelId) {
//    this.videoUrl = videoUrl;
//    this.title = title;
//    this.content = content;
//    this.privateType = privateType;
//    this.commentPermission = commentPermission;
//    this.thumbnail = thumbnail;
//    this.channelId = channelId;
//  }
}
