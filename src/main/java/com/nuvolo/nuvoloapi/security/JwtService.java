package com.nuvolo.nuvoloapi.security;

import com.nuvolo.nuvoloapi.model.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {


    @Value("${spring.application.name}")
    private String issuer;

    @Value("${security.jwt.key}")
    private String jwtKey;

    @Value("${security.jwt.expiration.minutes}")
    private Integer minutes;

    public String generateToken(String email, List<Role> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(email)
                .claim("roles", roles.stream().map(role -> role.getName().name()).toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(minutes, ChronoUnit.MINUTES)))
                .signWith(this.getSigningKey())
                .compact();
    }

    public String extractEmailFromToken(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public boolean validToken(String token, UserDetails userDetails) {
        Claims claims = this.extractAllClaims(token);
        try {
            return claims.getIssuedAt().before(claims.getExpiration()) &&
                    claims.getSubject().equals(userDetails.getUsername());
        } catch (Exception ex) {
            throw new JwtException("JWT is expired or invalid.");
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

}
