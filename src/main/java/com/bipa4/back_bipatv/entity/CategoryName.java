package com.bipa4.back_bipatv.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@Table(name = "CategoryNames")
public class CategoryName {

  @Id
  @GenericGenerator(name = "uuid4", strategy = "uuid2")
  @GeneratedValue(generator = "uuid4")
  @Column(columnDefinition = "BINARY(16)")
  private UUID categoryNameId;
  @Column(name = "name", nullable = false, length = 30)
  private String name;
}
