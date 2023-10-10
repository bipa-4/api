package com.bipa4.back_bipatv.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Data
@Table(name = "CategoryNames")
public class CategoryName {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(columnDefinition = "BINARY(16)")
  @Type(type = "uuid-binary")
  private UUID categoryNameId;
  @Column(name = "name", nullable = false, length = 30)
  private String name;
}
