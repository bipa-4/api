package com.bipa4.back_bipatv.entity;

import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;

@RedisHash(value = "refreshToken", timeToLive = 60)
public class RefreshToken {
    @Id
    private String refreshToken;
    private Long accountId;

    public RefreshToken(final String refreshToken, final Long accountId) {
        this.refreshToken = refreshToken;
        this.accountId = accountId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getMemberId() {
        return accountId;
    }
}

