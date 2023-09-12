package com.bipa4.back_bipatv.entity;

import javax.persistence.*;

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
