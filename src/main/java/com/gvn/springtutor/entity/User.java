package com.gvn.springtutor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity User - Merepresentasikan tabel 'users' di database.
 * 
 * PENJELASAN ANOTASI JPA:
 * 
 * @Entity - Menandai class ini sebagai JPA Entity.
 * 
 * @Table(name = "users") - Nama tabel di database.
 * 
 * @ManyToMany - Mendefinisikan relasi Many-to-Many dengan entity lain.
 *             Satu User bisa memiliki banyak Role, dan satu Role bisa dimiliki
 *             banyak User.
 * 
 * @JoinTable - Mendefinisikan tabel join (junction table) untuk relasi
 *            Many-to-Many:
 *            - name: nama tabel join ("user_roles")
 *            - joinColumns: kolom yang me-reference entity ini (user_id →
 *            users.id)
 *            - inverseJoinColumns: kolom yang me-reference entity lawan
 *            (role_id → roles.id)
 * 
 *            KENAPA TERBENTUK 3 TABEL?
 *            ========================
 *            Relasi Many-to-Many membutuhkan tabel perantara (junction/bridge
 *            table):
 * 
 *            1. USERS table → menyimpan data user
 *            2. ROLES table → menyimpan data role
 *            3. USER_ROLES table → menyimpan relasi user-role (user_id,
 *            role_id)
 * 
 *            Tabel USER_ROLES ini dibuat karena:
 *            - Tidak mungkin menyimpan multiple foreign key dalam satu kolom
 *            - Normalisasi database mengharuskan pemisahan relasi many-to-many
 *            - Memungkinkan satu user punya banyak role DAN satu role dimiliki
 *            banyak user
 * 
 *            FETCH TYPE:
 *            ===========
 *            FetchType.EAGER - Data langsung di-load bersamaan dengan entity
 *            parent.
 *            Digunakan di sini agar roles ikut ter-serialize saat get users.
 *            FetchType.LAZY - Data di-load hanya saat diakses (default
 *            untuk @ManyToMany).
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "is_active")
    private Boolean isActive;

    /**
     * Relasi Many-to-Many dengan Role.
     * 
     * FetchType.EAGER digunakan agar roles otomatis di-load saat query user,
     * sehingga tidak terjadi LazyInitializationException saat serialisasi ke JSON.
     * 
     * Tabel yang terbentuk: user_roles
     * Kolom: user_id (FK ke users.id), role_id (FK ke roles.id)
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}
