package com.github.mangila.springsecurityrestful.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@ConfigurationProperties(prefix = "application.security.jwt")
@Getter
@Setter
public class JwtProperties {
    private String key;
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration expiration;
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration refreshExpiration;
}