package com.gvn.springtutor.controller;

import com.gvn.springtutor.base.ApiResponse;
import com.gvn.springtutor.entity.User;
import com.gvn.springtutor.service.UserService;
import com.gvn.springtutor.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller untuk User.
 * 
 * Menggunakan ResponseUtil untuk standarisasi response API.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /users - Mengambil semua user beserta role.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseUtil.ok(users, "Users retrieved successfully");
    }

    /**
     * POST /users - Membuat user baru.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseUtil.created(savedUser, "User created successfully");
    }
}
