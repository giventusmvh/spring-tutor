package com.gvn.springtutor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Login Response DTO
 * 
 * Berisi JWT token dan informasi user setelah login berhasil.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type;
    private String username;
    private List<String> roles;
}
