package com.github.mangila.springsecurityrestful.service;

import com.github.mangila.springsecurityrestful.common.TokenException;
import com.github.mangila.springsecurityrestful.config.JwtProperties;
import com.github.mangila.springsecurityrestful.persistance.refresh.RefreshTokenEntity;
import com.github.mangila.springsecurityrestful.persistance.refresh.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtProperties properties;

    public String create(String username) {
        var r = new RefreshTokenEntity();
        r.setUsername(username);
        r.setRefreshToken(UUID.randomUUID().toString());
        r.setExpiration(Instant.now().plus(properties.getRefreshExpiration()));
        return repository.save(r).getRefreshToken();
    }

    public Optional<RefreshTokenEntity> findById(String refreshToken) {
        return repository.findById(refreshToken);
    }

    public boolean isExpired(RefreshTokenEntity entity) {
        if (entity.getExpiration().isBefore(Instant.now())) {
            throw new TokenException("Refresh token is expired");
        }
        return true;
    }
}
