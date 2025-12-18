package com.gvn.springtutor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity Role - Merepresentasikan tabel 'roles' di database.
 * 
 * PENJELASAN ANOTASI JPA:
 * 
 * @Entity - Menandai class ini sebagai JPA Entity yang akan di-mapping ke tabel
 *         database.
 *         Hibernate akan mengelola lifecycle object ini (persist, merge,
 *         remove, dll).
 * 
 * @Table(name = "roles") - Menentukan nama tabel di database. Jika tidak ada,
 *             akan menggunakan nama class (Role) sebagai nama tabel.
 * 
 * @Id - Menandai field sebagai primary key dari entity.
 * 
 * @GeneratedValue(strategy = GenerationType.IDENTITY) - Menentukan strategi
 *                          auto-generation untuk primary key.
 *                          - IDENTITY: menggunakan auto-increment database
 *                          (cocok untuk SQL Server)
 *                          - SEQUENCE: menggunakan database sequence
 *                          - TABLE: menggunakan tabel khusus untuk generate ID
 *                          - AUTO: Hibernate memilih strategi terbaik
 *                          berdasarkan database
 * 
 * @Column - Konfigurasi mapping kolom:
 *         - name: nama kolom di database
 *         - unique: constraint UNIQUE
 *         - nullable: constraint NOT NULL (false = NOT NULL)
 *         - length: panjang maksimum untuk String
 */
@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;
}
