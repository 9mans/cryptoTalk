package com.example.cryptotalk.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long validityInMilliseconds = 3600000;

    public String createToken(String userId, String email) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("email", email);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();

        logger.info("Generated JWT for userId: {}, email: {}, expires at: {}", userId, email, validity);
        logger.debug("JWT Token: {}", token);
        return token;

    }

    public String getUserId(String token) {
        try {
            String userId = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            logger.info("Extracted userId: {} from token", userId);
            return userId;
        } catch (JwtException exception) {
            logger.error("Failed to extract userId from token: {}", exception.getMessage());
            throw exception;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            logger.info("Valid JWT Token: {}", token);
            return true;
        } catch (ExpiredJwtException exception) {
            logger.warn("Expired JWT Token: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            logger.warn("Unsupported JWT Token: {}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            logger.warn("Malformed JWT Token: {}", exception.getMessage());
        } catch (SecurityException exception) {
            logger.warn("Invalid JWT Signature: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            logger.warn("Invalid JWT Token: {}", exception.getMessage());
        }
        return false;
    }
}
