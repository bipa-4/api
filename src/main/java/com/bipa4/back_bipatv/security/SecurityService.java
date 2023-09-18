package com.bipa4.back_bipatv.security;

import com.bipa4.back_bipatv.dao.AccountDAO;
import com.bipa4.back_bipatv.entity.Accounts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

  @Value("${security.secretKey}")
  private String SECRET_KEY;

  @Autowired
  AccountDAO accountDAO;

  public String createToken(Accounts accounts,
      long expTime) {//Id는 subject, PW는 SecretKey를 만드는데 사용 보통
    if (expTime <= 0) {
      throw new RuntimeException("만료시간이 0보다 커야함");
    }
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(
        SECRET_KEY);//SECRET_KEY byte타입으로 만들어서 넣어줌
    Key singingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());//key가 만들어짐

    return Jwts.builder()//지금은 subject값고 만료시간만 넣어줌 but 다양한 값 넣을 수 있음 확인해보기
        .setSubject(accounts.getLoginId())
        .signWith(singingKey, signatureAlgorithm)
        .claim("userId", accounts.getLoginId())
        .claim("nickName", accounts.getName())
        .setExpiration(new Date(System.currentTimeMillis() + expTime))//만료시간
        .compact();
  }

  public String createRefreshToken() {
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(
        SECRET_KEY);//SECRET_KEY byte타입으로 만들어서 넣어줌
    Key singingKey = new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());//key가 만들어짐

    return Jwts.builder()//지금은 subject값고 만료시간만 넣어줌 but 다양한 값 넣을 수 있음 확인해보기
        .setSubject("RefreshToken")
        .signWith(singingKey, signatureAlgorithm)
        .setExpiration(new Date(System.currentTimeMillis() + 24 * 1000 * 60 * 60))//만료시간
        .compact();
  }

  public boolean getSubject(String token) {//------------------valid 토큰 accessToken검증

    Claims claims = Jwts.parserBuilder()
        .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
        .build()
        .parseClaimsJws(token)
        .getBody();
    Accounts dummyAccount = new Accounts();
    dummyAccount.setLoginId(claims.getSubject());
    return accountDAO.findAccount(dummyAccount);
  }
}
