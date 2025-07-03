package com.ecopreco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String fullName;
    private Set<String> roles;

    public AuthResponse(String token, String username, String email, String fullName, Set<String> roles) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public AuthResponse(String token, String username, String email, String fullName) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }
}