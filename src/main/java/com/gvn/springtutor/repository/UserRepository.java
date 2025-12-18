package com.gvn.springtutor.repository;

import com.gvn.springtutor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository untuk entity User.
 * 
 * ALUR ORM DARI REQUEST SAMPAI DATABASE:
 * ======================================
 * 
 * 1. REQUEST masuk ke Controller (HTTP Request)
 * ↓
 * 2. Controller memanggil method di Service Layer
 * ↓
 * 3. Service memanggil method di Repository
 * ↓
 * 4. Repository (JpaRepository) menggunakan EntityManager untuk:
 * - Mengkonversi method call menjadi JPQL/SQL query
 * - Mengirim query ke database via JDBC
 * ↓
 * 5. Database mengeksekusi query dan mengembalikan result set
 * ↓
 * 6. Hibernate memetakan result set ke Java Object (Entity)
 * ↓
 * 7. Entity dikembalikan ke Service → Controller → JSON Response
 * 
 * HIBERNATE SEBAGAI ORM:
 * =====================
 * - Hibernate adalah implementasi dari JPA (Java Persistence API)
 * - Menghandle mapping antara Java Object dan Database Table
 * - Menggenerate SQL query secara otomatis
 * - Mengelola session, caching, dan transaction
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Mencari User berdasarkan username.
     * Spring Data JPA akan otomatis generate query:
     * SELECT * FROM users WHERE username = :username
     * 
     * @param username username yang dicari
     * @return Optional<User> (bisa kosong jika tidak ditemukan)
     */
    Optional<User> findByUsername(String username);
}
