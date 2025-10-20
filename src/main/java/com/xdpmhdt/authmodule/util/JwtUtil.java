package com.xdpmhdt.authmodule.util;

import com.xdpmhdt.authmodule.config.JwtConfig;
import com.xdpmhdt.authmodule.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private UserRepository userRepository;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add userId and role to claims for cross-service authentication
        if (userDetails instanceof org.springframework.security.core.userdetails.User) {
            // Get actual user from username
            com.xdpmhdt.authmodule.entity.User user = extractUserFromDetails(userDetails);
            if (user != null) {
                claims.put("userId", user.getId());
                claims.put("role", user.getRole().name());
            }
        }
        return createToken(claims, userDetails.getUsername(), jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), jwtConfig.getRefreshTokenExpiration());
    }
    
    // Helper method to extract User entity from UserDetails
    private com.xdpmhdt.authmodule.entity.User extractUserFromDetails(UserDetails userDetails) {
        try {
            // This will be injected via constructor
            return userRepository.findByUsername(userDetails.getUsername()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

