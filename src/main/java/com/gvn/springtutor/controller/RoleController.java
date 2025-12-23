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

    /**
     * GET /roles/{id} - Mengambil role berdasarkan ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return ResponseUtil.ok(role, "Role retrieved successfully");
    }

    /**
     * PUT /roles/{id} - Update role berdasarkan ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(id, role);
        return ResponseUtil.ok(updatedRole, "Role updated successfully");
    }

    /**
     * DELETE /roles/{id} - Hapus role berdasarkan ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseUtil.ok(null, "Role deleted successfully");
    }
}
