package com.github.mangila.springsecurityrestful.persistance.refresh;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
}
