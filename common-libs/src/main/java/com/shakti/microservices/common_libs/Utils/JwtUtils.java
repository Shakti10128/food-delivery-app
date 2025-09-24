package com.shakti.microservices.common_libs.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;

public class JwtUtils {

    // Secret key for signing tokens
    @Value("${jwt-secret-key}")
    private static String SECRET_KEY;

    // Token validity in milliseconds (1 hour)
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    // ================== GENERATE TOKEN ==================
    public static String generateToken(String SECRET_KEY,String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)                 // role, other info
                .setSubject(subject)               // usually email/username
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .compact();
    }


    // ================== VALIDATE TOKEN ==================
    public static boolean validateToken(String token, String subject) {
        final String tokenSubject = extractSubject(token);
        return (tokenSubject.equals(subject) && !isTokenExpired(token));
    }

    public static boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // ================== EXTRACT CLAIMS ==================
    public static String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public static String extractRole(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    private static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}