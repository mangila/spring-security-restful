package com.github.mangila.springsecurityrestful.web;

import com.github.mangila.springsecurityrestful.persistance.user.UserEntity;
import com.github.mangila.springsecurityrestful.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/basic")
@AllArgsConstructor
public class BasicController {

    private final UserService service;

    @GetMapping("me")
    public ResponseEntity<UserEntity> me() {
        var username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        return ResponseEntity.ok(service.findById(username));
    }

}
