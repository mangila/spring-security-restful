package com.github.mangila.springsecurityrestful.persistance.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> authorities;
    private boolean isEnabled;
}
