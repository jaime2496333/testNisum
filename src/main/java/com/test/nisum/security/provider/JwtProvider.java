package com.test.nisum.security.provider;


import com.test.nisum.domain.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import static io.jsonwebtoken.Jwts.parserBuilder;

@Component
public class JwtProvider {

    private final String authoritiesKey;

    private final String secretKey;

    private final long expirationMilliseconds;

    @Autowired
    public JwtProvider(@Value("${security.jwt.token.authorities.key}") String authoritiesKey,
                       @Value("${security.jwt.token.secret-key}") String secretKey,
                       @Value("${security.jwt.token.expiration}") long expirationMilliseconds) {
        this.authoritiesKey = authoritiesKey;
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.expirationMilliseconds = expirationMilliseconds;
    }

    public String generateToken(String username, List<Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(authoritiesKey,
                roles.stream().map(
                        role ->new SimpleGrantedAuthority(role.getAuthority())
                ).collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expirationMilliseconds))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> getPayload(String token) {
            return getJwtParserBuilder().build().parseClaimsJws(token);
    }

    public String getUsername(String token) {
        return getPayload(token).getBody().getSubject();
    }

    @SuppressWarnings(value = "unchecked")
    public List<GrantedAuthority> getRoles(String token) {
        List<Map<String, String>> roleClaims = getPayload(token).getBody().get(authoritiesKey, List.class);
        return roleClaims.stream().map(roleClaim ->
                        new SimpleGrantedAuthority(roleClaim.get("authority")))
                .collect(Collectors.toList());
    }

    public Date getExpirationDate(String token) {
        return this.getPayload(token).getBody().getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDate(token);
        return expirationDate.before(new Date());
    }

    public Boolean validateToken(String token){
        return (Objects.nonNull(getUsername(token)) && !isTokenExpired(token));
    }

    private JwtParserBuilder getJwtParserBuilder() {
        return parserBuilder().setSigningKey(getKey());
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
