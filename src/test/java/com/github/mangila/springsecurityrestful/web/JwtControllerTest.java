package com.github.mangila.springsecurityrestful.web;

import com.github.mangila.springsecurityrestful.persistance.user.UserEntity;
import com.github.mangila.springsecurityrestful.persistance.user.UserRepository;
import com.github.mangila.springsecurityrestful.web.model.RefreshTokenRequest;
import com.github.mangila.springsecurityrestful.web.model.TokenResponse;
import com.github.mangila.springsecurityrestful.web.model.UsernameAndPasswordRequest;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class JwtControllerTest {

    @Autowired
    private WebTestClient http;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;

    @BeforeEach
    void populate() {
        var u = new UserEntity();
        u.setUsername("mangila");
        u.setPassword(passwordEncoder.encode("password"));
        u.setAuthorities(List.of("ROLE_ADMIN", "ROLE_USER"));
        u.setEnabled(Boolean.TRUE);
        repository.save(u);
    }

    @AfterEach
    void truncate() {
        repository.deleteAll();
    }

    @Test
    void authorize() {
        http.post()
                .uri("/api/v1/jwt/token")
                .bodyValue(new UsernameAndPasswordRequest("mangila", "password"))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    void authorizeWrongPassword() {
        http.post()
                .uri("/api/v1/jwt/token")
                .bodyValue(new UsernameAndPasswordRequest("mangila", "wrong-password"))
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    void authorizeUserNotExist() {
        http.post()
                .uri("/api/v1/jwt/token")
                .bodyValue(new UsernameAndPasswordRequest("not-exists", "password"))
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }

    @Test
    void refresh() {
        var refreshToken = http.post()
                .uri("/api/v1/jwt/token")
                .bodyValue(new UsernameAndPasswordRequest("mangila", "password"))
                .exchange()
                .returnResult(TokenResponse.class)
                .getResponseBody()
                .blockLast()
                .refreshToken();

        http.post()
                .uri("/api/v1/jwt/refresh")
                .bodyValue(new RefreshTokenRequest(refreshToken))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    void me() {
        var jwt = http.post()
                .uri("/api/v1/jwt/token")
                .bodyValue(new UsernameAndPasswordRequest("mangila", "password"))
                .exchange()
                .returnResult(TokenResponse.class)
                .getResponseBody()
                .blockLast()
                .jwt();
        http.get()
                .uri("/api/v1/jwt/me")
                .headers(headers -> headers.setBearerAuth(jwt))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(UserEntity.class)
                .value(UserEntity::getUsername, Matchers.equalTo("mangila"))
                .value(UserEntity::getAuthorities, Matchers.hasSize(2))
                .value(UserEntity::isEnabled, Matchers.equalTo(true));
    }
}