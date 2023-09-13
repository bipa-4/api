package com.bipa4.back_bipatv.repository;
import com.bipa4.back_bipatv.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 60L; // 토큰 만료 시간 (60초)

    private final RedisTemplate<String, Long> redisTemplate;

    @Autowired
    public RefreshTokenRepository(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Refresh Token을 Redis에 저장합니다.
    public void save(RefreshToken refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getMemberId(), REFRESH_TOKEN_EXPIRATION_SECONDS, TimeUnit.SECONDS);
    }

    // Refresh Token을 Redis에서 검색합니다.
    public Optional<RefreshToken> findById(String refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        Long memberId = valueOperations.get(refreshToken);

        if (memberId != null) {
            return Optional.of(new RefreshToken(refreshToken, memberId));
        } else {
            return Optional.empty();
        }
    }
}
