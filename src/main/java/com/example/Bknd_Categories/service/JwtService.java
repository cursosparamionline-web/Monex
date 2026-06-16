package com.example.Bknd_Categories.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Long getUserIdFromAuthorizationHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token inválido o no enviado");
        }

        String token = authHeader.substring(7);

        if (token.isBlank()) {
            throw new IllegalArgumentException("Token vacío");
        }

        try {
            Claims claims = extractClaims(token);
            Object userIdClaim = claims.get("userId");

            if (userIdClaim instanceof Number userId) {
                return userId.longValue();
            }

            if (userIdClaim instanceof String userIdText) {
                return Long.parseLong(userIdText);
            }

            throw new IllegalArgumentException("El token no contiene un userId válido");

        } catch (Exception ex) {
            throw new IllegalArgumentException("Token inválido");
        }
    }

    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}