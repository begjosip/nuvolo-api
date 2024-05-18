package com.nuvolo.nuvoloapi.util;

import com.nuvolo.nuvoloapi.model.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@UtilityClass
public class JwtUtil {

    private static final String ISSUER = "nuvolo";

    /* Key for signing token */
    private static final String JWT_KEY = "2KNWCK1PM3ADY2H0ZMRG2JLNCA3B8Y9PYNRRE1NPSRDY66XTHAERK1WS12FEAP9N3XYAAVQ4Q7ZZCTGHZ6SHGE364US4Q0UMBC9SG6KV0J1MFE3EFQHY6RAELBNHNMHZ09VF79YJT3E854MLNK82CCXD3FHR169C2PH27P36AW8HQM1XJWSW1FZA2RWMX6QH2PK4MU8D50YU9VHDBLA0Y3TKC7SFCT9F6RRBNDGBRADZQEDXTBA5EKPZ4GADQM04UZ7BRACR874544LXZM0KBNEF2RRF5404G3301QF45EDJLN9CKWHK1MV8U7JX2G5ZQSNGCXMFYEC2UNMRSN1EU39Q71C0EHBFR89PCH85FFKD205992842C9YKSRWZVY8RZB4DHM99EN4YPLQ809M30MTL6R83QW6YHPPY2463NLREB1WWNYW7AY8B1SLPHDXTSETK8ZMU3R3DLUN92YAN3T1M0RHVPP6FZZ0KC54KJP58CVR6LA03MDNW1Y9HEZ9";

    /* JWT token expiration time */
    private static final int MINUTES = 15;

    public static String generateToken(String email, List<Role> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(email)
                .claim("roles", roles.stream().map(role -> role.getName().name()).toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(MINUTES, ChronoUnit.MINUTES)))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        try {
            return claims.getIssuedAt().before(claims.getExpiration()) &&
                    claims.getSubject().equals(userDetails.getUsername());
        } catch (Exception ex) {
            throw new JwtException("JWT is expired or invalid.");
        }
    }

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));
    }
}
