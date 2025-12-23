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

    /**
     * GET /users/{id} - Mengambil user berdasarkan ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseUtil.ok(user, "User retrieved successfully");
    }

    /**
     * PUT /users/{id} - Update user berdasarkan ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseUtil.ok(updatedUser, "User updated successfully");
    }

    /**
     * DELETE /users/{id} - Hapus user berdasarkan ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseUtil.ok(null, "User deleted successfully");
    }
}
