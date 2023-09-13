package com.bipa4.back_bipatv.entity;


import com.bipa4.back_bipatv.dataType.ELogin_Type;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Accounts")
@NoArgsConstructor
public class Accounts {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "account_id")
  private Long accountId;
  @Column(name = "join_date", nullable = false)
  private Timestamp joinDate;
  @Column(name = "login_type", nullable = false, length = 20)
  @Enumerated(value = EnumType.STRING)
  private ELogin_Type loginType;
  @Column(name = "login_Id", nullable = false, length = 100)
  private String loginId;
  @Column(name = "name", nullable = false, length = 20)
  private String name;
  @Column(name = "profile_url", nullable = true, length = 200)
  private String profileUrl;
  @Column(name = "email", nullable = true, length = 40)
  private String eMail;
  @Column(name = "refresh_token", nullable = true, length = 300)//redis사용할 때 nullable=false로 바꾸기
  private String refreshToken;

}
