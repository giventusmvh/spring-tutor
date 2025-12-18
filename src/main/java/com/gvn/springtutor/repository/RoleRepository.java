package com.gvn.springtutor.repository;

import com.gvn.springtutor.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository untuk entity Role.
 * 
 * PENJELASAN:
 * 
 * @Repository - Menandai interface ini sebagai Spring Repository Bean.
 *             Sebenarnya tidak wajib karena JpaRepository sudah otomatis
 *             terdeteksi,
 *             tapi menambahkan clarity tentang fungsi interface ini.
 * 
 *             JpaRepository<Role, Long> - Interface dari Spring Data JPA yang
 *             menyediakan:
 *             - Role: tipe entity yang dikelola
 *             - Long: tipe primary key entity
 * 
 *             METHOD BAWAAN JpaRepository:
 *             - save(entity): insert atau update entity
 *             - findById(id): cari entity berdasarkan ID
 *             - findAll(): ambil semua entity
 *             - delete(entity): hapus entity
 *             - count(): hitung jumlah record
 *             - existsById(id): cek apakah entity dengan ID tertentu ada
 *             - dan banyak lagi...
 * 
 *             QUERY METHOD:
 *             Spring Data JPA auto-generate query berdasarkan nama method.
 *             findByName(String name) → SELECT * FROM roles WHERE name = ?
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Mencari Role berdasarkan nama.
     * Spring Data JPA akan otomatis generate query:
     * SELECT * FROM roles WHERE name = :name
     * 
     * @param name nama role yang dicari
     * @return Optional<Role> (bisa kosong jika tidak ditemukan)
     */
    Optional<Role> findByName(String name);
}
