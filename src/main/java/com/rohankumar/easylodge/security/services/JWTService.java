package com.rohankumar.easylodge.security.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTService {

    @Value("${app.security.jwt.secret.key}")
    private String jwtSecret;
    @Value("${app.security.jwt.access.token.expiration}")
    private Long accessTokenExpiration;
    @Value("${app.security.jwt.refresh.token.expiration}")
    private Long refreshTokenExpiration;

    public String generateAccessToken(UserDetails user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities());
        return generateToken(claims, user, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails user) {

        Map<String, Object> claims = new HashMap<>();
        return generateToken(claims, user, refreshTokenExpiration);
    }

    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails user) {

        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    private Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSecreteKey() {

        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(Map<String, Object> claims, UserDetails user, Long accessTokenExpiration) {

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSecreteKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSecreteKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
