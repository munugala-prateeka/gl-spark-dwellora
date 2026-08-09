package com.dwellora.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating and handling JSON Web Tokens (JWTs).
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT containing user identity, role, and optional apartment information.
     */
    public String generateToken(
            Long userId, String email, String role, Long apartmentId) {

        var builder =
                Jwts.builder()
                        .subject(email)
                        .claim("userId", userId)
                        .claim("role", role)
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + expiration));

        if (apartmentId != null) {
            builder.claim("apartmentId", apartmentId);
        }

        return builder.signWith(getKey()).compact();
    }
}