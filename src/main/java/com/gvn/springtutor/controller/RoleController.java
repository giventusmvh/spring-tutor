package com.gvn.springtutor.controller;

import com.gvn.springtutor.entity.Role;
import com.gvn.springtutor.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller untuk Role.
 * 
 * PENJELASAN ANOTASI:
 * 
 * @RestController - Kombinasi dari @Controller dan @ResponseBody.
 *                 Menandai class ini sebagai REST Controller yang
 *                 mengembalikan data JSON langsung (bukan view).
 * 
 *                 @RequestMapping("/roles") - Base URL path untuk semua
 *                 endpoint di controller ini.
 * 
 * @GetMapping - Menangani HTTP GET request.
 * 
 * @PostMapping - Menangani HTTP POST request.
 * 
 * @RequestBody - Mengkonversi JSON request body menjadi Java object.
 * 
 *              ResponseEntity<T> - Wrapper untuk HTTP response yang
 *              memungkinkan
 *              kita mengatur status code dan headers.
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * GET /roles - Mengambil semua role.
     * 
     * Contoh response:
     * [
     * {"id": 1, "name": "ADMIN"},
     * {"id": 2, "name": "USER"}
     * ]
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * POST /roles - Membuat role baru.
     * 
     * Contoh request body:
     * {
     * "name": "MANAGER"
     * }
     * 
     * Contoh response:
     * {
     * "id": 3,
     * "name": "MANAGER"
     * }
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role savedRole = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }
}
