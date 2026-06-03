package com.qmaserver.quantitymeasurement.auth;

import com.qmaserver.quantitymeasurement.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class JwtService {
    private static final Logger log = LogManager.getLogger(JwtService.class);
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenPayload generateToken(UserEntity userEntity) {
        log.trace("Starting token generation");
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(jwtExpirationMs);
        String token = Jwts.builder()
                .subject(userEntity.getEmail())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .claims(Map.of(
                        "userId", userEntity.getId(),
                        "name", userEntity.getName()))
                .signWith(secretKey)
                .compact();
        return new TokenPayload(token, issuedAt, expiresAt);
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration().after(new Date());
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    private Claims extractClaims(String token) {
        log.trace("Starting claims extraction");
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.fatal("Extraction failed: Token signature invalid");
            throw e;
        }
    }

    public record TokenPayload(String token, Instant issuedAt, Instant expiresAt) {
    }
}
