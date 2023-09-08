package com.bipa4.back_bipatv.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Logs")
public class Logs {
    @Id
    @GeneratedValue
    @Column(name="log_id")
    private int logId;
    @Column(name = "date",nullable = false)
    private Timestamp date;
    @Column(name = "content",nullable = false,length = 400)
    private String content;
    @Column(name = "log_function",nullable = false,length = 200)
    private String logFunction;
    @Column(name = "account_id")
    private String accountId;

}
