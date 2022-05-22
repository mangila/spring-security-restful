package com.github.mangila.springsecurityrestful.security;


import com.github.mangila.springsecurityrestful.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenProvider {

    public final static String ISSUER_VALUE = "mangila@github";
    public final static String AUDIENCE_VALUE = "https://github.com";
    public final static String ID_VALUE = UUID.randomUUID().toString();
    public final static String ROLES_KEY = "roles";
    private final JwtProperties jwtProperties;
    private final Key key;
    private final JwtParser parser;

    public TokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] bytes = jwtProperties.getKey().getBytes();
        this.key = Keys.hmacShaKeyFor(bytes);
        this.parser = Jwts.parserBuilder()
                .requireIssuer(ISSUER_VALUE)
                .requireAudience(AUDIENCE_VALUE)
                .requireId(ID_VALUE)
                .setSigningKey(key)
                .build();
    }

    public String generate(String name, Collection<? extends GrantedAuthority> authorities) {
        final var issuedAt = Date.from(Instant.now());
        final var expiration = Date.from(Instant.now().plus(jwtProperties.getExpiration()));
        final var auths = authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .setIssuer(ISSUER_VALUE)
                .setAudience(AUDIENCE_VALUE)
                .setId(ID_VALUE)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setSubject(name)
                .claim(ROLES_KEY, auths)
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String jwt) {
        return parser.parseClaimsJws(jwt);
    }
}
