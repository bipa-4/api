package com.bipa4.back_bipatv.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name="Videos")
public class Videos {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="video_id")
    private int videoId;
    @Column(name="video_url",nullable = true, length = 200)
    private String videoUrl;
    @Column(name="title",nullable = true, length = 100)
    private String title;
    @Column(name="content",nullable = true, length = 400)
    private String content;
    @Column(name="read_cnt",nullable = true)
    private int readCnt;
    @Column(name="create_at",nullable = true)
    private Timestamp createAt;
    @Column(name="private_type")
    private boolean privateType;
    @Column(name="comment_permission")
    private boolean commentPermission;
    @Column(name="thumbnail",nullable = true, length = 200)
    private String thumbnail;
    @Column(name="channel_id")
    private int channelId;


}
