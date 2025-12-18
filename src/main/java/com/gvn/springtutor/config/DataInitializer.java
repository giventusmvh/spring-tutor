package com.gvn.springtutor.config;

import com.gvn.springtutor.entity.Role;
import com.gvn.springtutor.entity.User;
import com.gvn.springtutor.repository.RoleRepository;
import com.gvn.springtutor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import com.gvn.springtutor.entity.Product;
import com.gvn.springtutor.repository.ProductRepository;

/**
 * Data Initializer - Menginisialisasi data awal saat aplikasi start.
 * 
 * PENJELASAN:
 * 
 * @Component - Menandai class ini sebagai Spring Bean yang akan di-manage oleh
 *            Spring Container.
 * 
 *            CommandLineRunner - Interface dari Spring Boot yang method
 *            run()-nya akan
 *            dieksekusi setelah ApplicationContext ter-load.
 *            Cocok untuk inisialisasi data, migrasi, atau setup awal.
 * 
 * @Slf4j - Lombok annotation untuk logging menggunakan SLF4J.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Initializing Data ===");

        // Create roles jika belum ada
        Role adminRole = createRoleIfNotExists("ADMIN");
        Role userRole = createRoleIfNotExists("USER");

        // Create sample user dengan 2 role jika belum ada
        createUserIfNotExists("admin", "admin@example.com", "admin123", adminRole, userRole);

        createProductIfNotExists("Bronze", 12, 5.0);
        createProductIfNotExists("Silver", 24, 7.0);
        createProductIfNotExists("Gold", 36, 9.0);

        log.info("=== Data Initialization Complete ===");
    }

    /**
     * Membuat role jika belum ada di database.
     */
    private Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    log.info("Creating role: {}", roleName);
                    Role role = Role.builder()
                            .name(roleName)
                            .build();
                    return roleRepository.save(role);
                });
    }

    /**
     * Membuat user jika belum ada di database.
     */
    private void createUserIfNotExists(String username, String email, String password, Role... roles) {
        if (userRepository.findByUsername(username).isEmpty()) {
            log.info("Creating user: {}", username);

            Set<Role> roleSet = new HashSet<>();
            for (Role role : roles) {
                roleSet.add(role);
            }

            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .isActive(true)
                    .roles(roleSet)
                    .build();

            userRepository.save(user);
            log.info("User '{}' created with roles: {}", username, roleSet);
        } else {
            log.info("User '{}' already exists, skipping...", username);
        }
    }

    private void createProductIfNotExists(String name, Integer tenor, Double interestRate) {
        if (productRepository.findByName(name).isEmpty()) {
            log.info("Creating product: {}", name);
            Product product = Product.builder()
                    .name(name)
                    .tenor(tenor)
                    .interestRate(interestRate)
                    .build();
            productRepository.save(product);
            log.info("Product '{}' created", name);
        } else {
            log.info("Product '{}' already exists, skipping...", name);
        }
    }
}
