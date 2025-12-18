package com.gvn.springtutor.service;

import com.gvn.springtutor.entity.User;
import com.gvn.springtutor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service Layer untuk User.
 * 
 * ALUR LENGKAP ORM REQUEST → DATABASE:
 * ===================================
 * 
 * [CLIENT] --HTTP Request--> [CONTROLLER]
 * |
 * v
 * [SERVICE] <-- Business Logic
 * |
 * v
 * [REPOSITORY]
 * |
 * v
 * [EntityManager/Hibernate]
 * |
 * v
 * [JDBC Connection Pool]
 * |
 * v
 * [DATABASE]
 * 
 * CONTOH ALUR getAllUsers():
 * =========================
 * 1. Client memanggil GET /users
 * 2. UserController.getAllUsers() dipanggil
 * 3. UserService.getAllUsers() dipanggil
 * 4. UserRepository.findAll() dipanggil
 * 5. Hibernate generate SQL: SELECT * FROM users
 * 6. Hibernate juga generate untuk relasi:
 * SELECT * FROM user_roles ur JOIN roles r ON ur.role_id = r.id WHERE
 * ur.user_id IN (...)
 * 7. Result set di-mapping ke List<User> dengan roles terisi
 * 8. Response JSON dikembalikan ke client
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Membuat user baru.
     * 
     * Hibernate akan:
     * 1. INSERT INTO users (username, email, password, is_active) VALUES (...)
     * 2. Jika ada roles, INSERT INTO user_roles (user_id, role_id) VALUES (...)
     * 
     * @param user entity User yang akan disimpan
     * @return User yang sudah tersimpan (dengan ID yang di-generate)
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Mengambil semua user beserta role-nya.
     * 
     * Karena menggunakan FetchType.EAGER, roles akan langsung di-load.
     * 
     * @return List semua User dengan roles
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Mencari user berdasarkan username.
     * 
     * @param username username yang dicari
     * @return Optional<User>
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
