package com.github.mangila.springsecurityrestful.persistance.refresh;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity(name = "refresh_token")
@Getter
@Setter
public class RefreshTokenEntity {

    @Id
    private String refreshToken;
    private String username;
    private Instant expiration;
}
