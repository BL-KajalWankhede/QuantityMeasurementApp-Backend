package com.qmaserver.quantitymeasurement.auth.dto;

import java.time.Instant;

public class AuthResponse {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private long issuedAtEpochSeconds;
    private long expiresAtEpochSeconds;
    private long refreshExpiresAtEpochSeconds;
    private UserProfileResponse user;

    public AuthResponse(String accessToken, String refreshToken, Instant issuedAt, Instant expiresAt, Instant refreshExpiresAt, UserProfileResponse user) {
        this.tokenType = "Bearer";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.issuedAtEpochSeconds = issuedAt.getEpochSecond();
        this.expiresAtEpochSeconds = expiresAt.getEpochSecond();
        this.refreshExpiresAtEpochSeconds = refreshExpiresAt.getEpochSecond();
        this.user = user;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getIssuedAtEpochSeconds() {
        return issuedAtEpochSeconds;
    }

    public long getExpiresAtEpochSeconds() {
        return expiresAtEpochSeconds;
    }

    public long getRefreshExpiresAtEpochSeconds() {
        return refreshExpiresAtEpochSeconds;
    }

    public UserProfileResponse getUser() {
        return user;
    }
}
