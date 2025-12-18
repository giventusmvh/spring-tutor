package com.gvn.springtutor.controller;

import com.gvn.springtutor.entity.User;
import com.gvn.springtutor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller untuk User.
 * 
 * ENDPOINT TERSEDIA:
 * - GET /users → Mengambil semua user beserta role
 * - POST /users → Membuat user baru
 * 
 * SERIALISASI:
 * ============
 * Spring Boot menggunakan Jackson untuk serialisasi/deserialisasi JSON.
 * Karena User.roles menggunakan FetchType.EAGER, roles akan otomatis
 * di-serialize ke JSON response.
 * 
 * MENCEGAH INFINITE LOOP:
 * ======================
 * Tidak ada relasi bidirectional di sisi Role, sehingga tidak akan
 * terjadi infinite loop saat serialisasi. Jika ada @ManyToMany dari
 * Role ke User, perlu menggunakan @JsonIgnore atau @JsonManagedReference
 * dan @JsonBackReference.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /users - Mengambil semua user beserta role.
     * 
     * Contoh response:
     * [
     * {
     * "id": 1,
     * "username": "admin",
     * "email": "admin@example.com",
     * "password": "admin123",
     * "isActive": true,
     * "roles": [
     * {"id": 1, "name": "ADMIN"},
     * {"id": 2, "name": "USER"}
     * ]
     * }
     * ]
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * POST /users - Membuat user baru.
     * 
     * Contoh request body (tanpa role):
     * {
     * "username": "johndoe",
     * "email": "john@example.com",
     * "password": "secret123",
     * "isActive": true
     * }
     * 
     * Contoh request body (dengan role yang sudah ada):
     * {
     * "username": "janedoe",
     * "email": "jane@example.com",
     * "password": "secret456",
     * "isActive": true,
     * "roles": [
     * {"id": 1},
     * {"id": 2}
     * ]
     * }
     * 
     * Note: Untuk assign role, cukup kirim ID role yang sudah ada di database.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
