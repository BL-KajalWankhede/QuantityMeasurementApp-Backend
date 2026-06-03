package com.qmaapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class GatewayJwtService {
    private static final Logger log = LogManager.getLogger(GatewayJwtService.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractEmailIfValid(String token) {
        log.trace("Starting token validation");
        try {
            Jws<Claims> signedClaims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            String email = signedClaims.getPayload().getSubject();
            
            if (email != null && !email.isBlank()) {
                return email.trim().toLowerCase();
            } else {
                log.fatal("Validation failed: Token subject is missing");
            }
        } catch (Exception ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
        }
        
        return null;
    }
}
