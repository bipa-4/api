package com.bipa4.back_bipatv.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "view_log")
public class ViewLog {

  @Id
  @JoinColumn(name = "video_id")
  private Long videoId;

  @OneToOne(fetch = FetchType.EAGER)
  @MapsId //@MapsId 는 @id로 지정한 컬럼에 @OneToOne 이나 @ManyToOne 관계를 매핑시키는 역할
  @JoinColumn(name = "video_id")
  private Videos videos;

  @Column(name = "view_cnt")
  private int view_cnt;
}
