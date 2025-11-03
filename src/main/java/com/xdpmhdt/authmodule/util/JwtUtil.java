package com.xdpmhdt.authmodule.util;

import com.xdpmhdt.authmodule.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Autowired
    private JwtConfig jwtConfig;

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

    /**
     * Generate access token with appropriate audience based on user roles
     * @param userDetails User details containing roles
     * @return JWT access token with role-based audience
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();
        
        claims.put("roles", roles);
        claims.put("iss", "http://localhost:8080"); // Issuer: Auth Service
        
        List<String> audiences = determineAudiences(roles);
        claims.put("aud", audiences);
        
        return createToken(claims, userDetails.getUsername(), jwtConfig.getAccessTokenExpiration());
    }

    /**
     * Generate access token with specific audience(s)
     * @param userDetails User details
     * @param audience Specific audience (can be single service or comma-separated list)
     * @return JWT access token with specified audience
     */
    public String generateAccessTokenWithAudience(UserDetails userDetails, String audience) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();
        
        claims.put("roles", roles);
        claims.put("iss", "http://localhost:8080"); // Run hosted
        // claims.put("iss", "http://auth-service:8080"); Run in Docker
        
        List<String> audiences = Arrays.stream(audience.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        claims.put("aud", audiences);
        
        return createToken(claims, userDetails.getUsername(), jwtConfig.getAccessTokenExpiration());
    }

    /**
     * Determine appropriate audiences based on user roles
     * @param roles List of user roles
     * @return List of service audiences the token should be valid for
     */
    private List<String> determineAudiences(List<String> roles) {
        Set<String> audiences = new HashSet<>();
        
        for (String role : roles) {
            switch (role.toUpperCase()) {
                case "ADMIN":
                    audiences.add("admin-service");
                    audiences.add("marketplace-service");
                    audiences.add("carbon-module");
                    audiences.add("auth-service");
                    break;
                    
                case "CVA": 
                    audiences.add("admin-service");
                    audiences.add("carbon-module");
                    audiences.add("marketplace-service");
                    break;
                    
                case "EV_OWNER":
                    audiences.add("carbon-module");
                    audiences.add("marketplace-service");
                    audiences.add("auth-service");
                    break;
                    
                case "CC_BUYER": 
                    audiences.add("marketplace-service");
                    audiences.add("auth-service");
                    break;
                    
                default:

                    audiences.add("auth-service");
                    break;
            }
        }
        
        if (audiences.isEmpty()) {
            audiences.add("auth-service");
        }
        
        return new ArrayList<>(audiences);
    }

    /**
     * Extract audience from token
     * @param token JWT token
     * @return List of audiences
     */
    @SuppressWarnings("unchecked")
    public List<String> extractAudience(String token) {
        Claims claims = extractAllClaims(token);
        Object aud = claims.get("aud");
        
        if (aud instanceof List) {
            return (List<String>) aud;
        } else if (aud instanceof String) {
            return Collections.singletonList((String) aud);
        }
        
        return Collections.emptyList();
    }

    /**
     * Validate if token is valid for a specific service
     * @param token JWT token
     * @param serviceName Name of the service to validate against
     * @return true if token is valid for the service
     */
    public Boolean validateTokenForService(String token, String serviceName) {
        try {
            List<String> audiences = extractAudience(token);
            return audiences.contains(serviceName) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "http://localhost:8080");
        return createToken(claims, userDetails.getUsername(), jwtConfig.getRefreshTokenExpiration());
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

