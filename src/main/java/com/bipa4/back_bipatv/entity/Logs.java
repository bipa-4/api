package com.bipa4.back_bipatv.entity;

import java.sql.Timestamp;
import javax.persistence.*;

import lombok.Data;

@Entity
@Data
@Table(name = "Logs")
public class Logs {

  @Id
  @GeneratedValue
  @Column(name = "log_id")
  private int logId;
  @Column(name = "date", nullable = false)
  private Timestamp date;
  @Column(name = "content", nullable = false, length = 400)
  private String content;
  @Column(name = "log_function", nullable = false, length = 200)
  private String logFunction;
  @ManyToOne
  @JoinColumn(name = "account_id")
  private Accounts accounts;

}
