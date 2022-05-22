package com.github.mangila.springsecurityrestful.common;

import com.github.mangila.springsecurityrestful.persistance.user.UserEntity;
import com.github.mangila.springsecurityrestful.persistance.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("!test")
@AllArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        var u = new UserEntity();
        u.setUsername("user");
        u.setPassword(passwordEncoder.encode("password"));
        u.setAuthorities(List.of("ROLE_USER"));
        u.setEnabled(Boolean.TRUE);
        repository.save(u);
        var a = new UserEntity();
        a.setUsername("admin");
        a.setPassword(passwordEncoder.encode("password"));
        a.setAuthorities(List.of("ROLE_ADMIN", "ROLE_USER"));
        a.setEnabled(Boolean.TRUE);
        repository.save(a);
        log.info("Seeded database with user and admin");
    }
}
