package com.github.mangila.springsecurityrestful.web.model;

import java.util.List;

public record TokenResponse(String username, List<String> authorities, String refreshToken, String jwt) {
}
