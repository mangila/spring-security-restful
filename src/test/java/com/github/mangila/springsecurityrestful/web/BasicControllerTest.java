package com.github.mangila.springsecurityrestful.web;


import com.github.mangila.springsecurityrestful.persistance.user.UserEntity;
import com.github.mangila.springsecurityrestful.persistance.user.UserRepository;
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
class BasicControllerTest {

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
    void me() {
        this.http
                .get()
                .uri("/api/v1/basic/me")
                .headers(headers -> headers.setBasicAuth("mangila", "password"))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(UserEntity.class)
                .value(UserEntity::getUsername, Matchers.equalTo("mangila"))
                .value(UserEntity::getAuthorities, Matchers.hasSize(2))
                .value(UserEntity::isEnabled, Matchers.equalTo(true));

    }

    @Test
    void meWrongPassword() {
        this.http
                .get()
                .uri("/api/v1/basic/me")
                .headers(headers -> headers.setBasicAuth("mangila", "wrong-password"))
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }
}