package com.bipa4.back_bipatv.repository;

import com.bipa4.back_bipatv.entity.RefreshToken;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository {

  private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 60 * 60 * 24L; // 토큰 만료 시간 (10일)

  private final RedisTemplate<String, String> redisTemplate;

  @Autowired
  public RedisRepository(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  // Refresh Token을 Redis에 저장합니다.
  public void save(RefreshToken refreshToken) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getMemberId(),
        REFRESH_TOKEN_EXPIRATION_SECONDS, TimeUnit.SECONDS);
  }

  // Refresh Token을 Redis에서 검색합니다.
  public Optional<RefreshToken> findById(String refreshToken) {
    ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
    String memberId = valueOperations.get(refreshToken);

    if (memberId != null) {
      return Optional.of(new RefreshToken(refreshToken, memberId));
    } else {
      return Optional.empty();
    }
  }

}
