package com.example.concert.common;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    private String createJwt(String email, String role, long expiredMs, String tokenType) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .claim("tokenType", tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiredMs)))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Access Token 발급
     */
    public String createAccessToken(String email, String role) {
        return createJwt(email, role, accessTokenExpiration, "ACCESS");
    }

    /**
     * Refresh Token 발급
     */
    public String createRefreshToken(String email, String role) {
        return createJwt(email, role, refreshTokenExpiration, "REFRESH");
    }
}
