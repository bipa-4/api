package com.bipa4.back_bipatv.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Categorys")
public class Categorys {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private int categoryId;
  @Column(name = "name", nullable = false, length = 30)
  private String name;
  @ManyToOne
  @JoinColumn(name = "video_id")
  private Videos videos;
}
