package com.mygroceries.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    // Put this in application.properties later
    private static final SecretKey KEY =
            Keys.hmacShaKeyFor("CHANGE_ME_TO_A_32+_CHAR_SECRET_KEY!!!!!!!!".getBytes());

    private static final long EXP_MS = 1000L * 60 * 60 * 24; // 24 hours

    public static String generate(String userId, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(userId) // sub = userId
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
    }
}