package com.bipa4.back_bipatv.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "Categorys")
public class Categorys {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;
    @Column(name="name",nullable = false,length = 30)
    private String name;
    @Column(name="video_id")
    private int videoId;
}
