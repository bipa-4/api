package com.bipa4.back_bipatv.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "CategoryNames")
public class CategoryName {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_name_id")
  private Long categoryNameId;
  @Column(name = "name", nullable = false, length = 30)
  private String name;
}
