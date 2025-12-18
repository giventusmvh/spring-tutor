package com.gvn.springtutor.service;

import com.gvn.springtutor.entity.Role;
import com.gvn.springtutor.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service Layer untuk Role.
 * 
 * PENJELASAN:
 * 
 * @Service - Menandai class ini sebagai Spring Service Bean.
 *          Service layer berisi business logic aplikasi.
 * 
 * @RequiredArgsConstructor - Lombok annotation yang generate constructor
 *                          untuk semua field yang final.
 *                          Ini adalah cara dependency injection yang
 *                          recommended.
 * 
 *                          TANGGUNG JAWAB SERVICE LAYER:
 *                          ============================
 *                          - Menampung business logic
 *                          - Mengkoordinasikan repository calls
 *                          - Transaction management (jika diperlukan)
 *                          - Validasi business rules
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Membuat role baru.
     * 
     * @param role entity Role yang akan disimpan
     * @return Role yang sudah tersimpan (dengan ID yang di-generate)
     */
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Mengambil semua role.
     * 
     * @return List semua Role
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Mencari role berdasarkan nama.
     * 
     * @param name nama role
     * @return Optional<Role>
     */
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
