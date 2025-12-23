package com.gvn.springtutor.service;

import com.gvn.springtutor.entity.Role;
import com.gvn.springtutor.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Role Service dengan Redis Caching.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Create role dan hapus cache.
     */
    @CacheEvict(value = "roles", allEntries = true)
    public Role createRole(Role role) {
        log.info("Creating role: {}", role.getName());
        return roleRepository.save(role);
    }

    /**
     * Get all roles dengan caching.
     */
    @Cacheable(value = "roles", key = "'allRoles'")
    public List<Role> getAllRoles() {
        log.info("Fetching all roles from DATABASE");
        return roleRepository.findAll();
    }

    /**
     * Find role by name dengan caching.
     */
    @Cacheable(value = "roles", key = "#name")
    public Optional<Role> findByName(String name) {
        log.info("Fetching role '{}' from DATABASE", name);
        return roleRepository.findByName(name);
    }

    /**
     * Get role by ID dengan caching.
     */
    @Cacheable(value = "roles", key = "'role_' + #id")
    public Role getRoleById(Long id) {
        log.info("Fetching role with ID {} from DATABASE", id);
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }
}
