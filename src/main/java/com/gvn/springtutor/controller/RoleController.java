package com.gvn.springtutor.controller;

import com.gvn.springtutor.base.ApiResponse;
import com.gvn.springtutor.entity.Role;
import com.gvn.springtutor.service.RoleService;
import com.gvn.springtutor.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller untuk Role.
 * 
 * Menggunakan ResponseUtil untuk standarisasi response API.
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * GET /roles - Mengambil semua role.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseUtil.ok(roles, "Roles retrieved successfully");
    }

    /**
     * POST /roles - Membuat role baru.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
        Role savedRole = roleService.createRole(role);
        return ResponseUtil.created(savedRole, "Role created successfully");
    }
}
