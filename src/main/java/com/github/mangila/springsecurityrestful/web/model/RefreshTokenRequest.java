package com.github.mangila.springsecurityrestful.web.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record RefreshTokenRequest(@NotBlank @Pattern(regexp = "^.{36}", message = "Length must be 36 chars") String refreshToken) {
}
