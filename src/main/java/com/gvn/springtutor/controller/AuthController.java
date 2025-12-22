package com.gvn.springtutor.controller;

import com.gvn.springtutor.base.ApiResponse;
import com.gvn.springtutor.dto.AuthRequest;
import com.gvn.springtutor.dto.AuthResponse;
import com.gvn.springtutor.dto.RegisterRequest;
import com.gvn.springtutor.service.AuthService;
import com.gvn.springtutor.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * 
 * Endpoints:
 * - POST /auth/login - Login dan dapatkan JWT token
 * - POST /auth/register - Register user baru
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint.
     * 
     * Request body:
     * {
     * "username": "admin",
     * "password": "admin123"
     * }
     * 
     * Response:
     * {
     * "success": true,
     * "message": "Login successful",
     * "data": {
     * "token": "eyJhbGciOiJIUzI1NiJ9...",
     * "type": "Bearer",
     * "username": "admin",
     * "roles": ["ADMIN", "USER"]
     * },
     * "code": 200,
     * "timestamp": "..."
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse authResponse = authService.login(request);
            return ResponseUtil.ok(authResponse, "Login successful");
        } catch (Exception e) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    /**
     * Register endpoint.
     * 
     * Request body:
     * {
     * "username": "newuser",
     * "email": "newuser@example.com",
     * "password": "password123"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse authResponse = authService.register(request);
            return ResponseUtil.created(authResponse, "User registered successfully");
        } catch (RuntimeException e) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
