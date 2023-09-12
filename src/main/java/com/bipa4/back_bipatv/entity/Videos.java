package com.bipa4.back_bipatv.entity;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name = "Videos")
public class Videos {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "video_id")
  private Long videoId;

  @Column(name = "video_url", nullable = true, length = 200)
  private String videoUrl;

  @Column(name = "title", nullable = true, length = 100)
  private String title;

  @Column(name = "content", nullable = true, length = 400)
  private String content;

  @Column(name = "read_cnt", nullable = true)
  private int readCnt;

  @CreationTimestamp
  @Column(name = "create_at")
  private LocalDateTime createAt = LocalDateTime.now();

  @Column(name = "private_type")
  private boolean privateType;

  @Column(name = "comment_permission")
  private boolean commentPermission;

  @Column(name = "thumbnail", nullable = true, length = 200)
  private String thumbnail;

  @Column(name = "channel_id")
  private int channelId;

  @Builder
  public Videos(String videoUrl, String title, String content, boolean privateType,
      boolean commentPermission, String thumbnail, int channelId) {
    this.videoUrl = videoUrl;
    this.title = title;
    this.content = content;
    this.privateType = privateType;
    this.commentPermission = commentPermission;
    this.thumbnail = thumbnail;
    this.channelId = channelId;
  }
}
