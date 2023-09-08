package com.bipa4.back_bipatv.entity;



import com.bipa4.back_bipatv.dataType.LoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name="Accounts")
@NoArgsConstructor
public class Accounts {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="account_id")
    private Long accountId;
    @Column(name="join_date", nullable=true)
    private Timestamp joinDate;
    @Column(name="login_type", nullable=true, length=20)
    private LoginType loginType;
    @Column(name="id", nullable=true, length=20)
    private String id;
    @Column(name="name", nullable=true, length=20)
    private String name;
    @Column(name="profile_url", nullable=true, length=200)
    private String profileUrl;
}
