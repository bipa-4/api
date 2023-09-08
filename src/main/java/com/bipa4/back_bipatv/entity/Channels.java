package com.bipa4.back_bipatv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="Channels")
public class Channels {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="channel_id")
    private int channelId;
    @Column(name="name",nullable = false)
    private String name;
    @Column(name="content",nullable = true)
    private String content;
    @Column(name="private_type",nullable = true)
    private boolean privateType;
    @Column(name="profile_url",nullable = true)
    private String profileUrl;

    @Column(name="account_id",nullable = false)
    private int accountId;
}
