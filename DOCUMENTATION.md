# Spring Boot REST API - Dokumentasi Lengkap

## Daftar Isi

1. [Overview Project](#overview-project)
2. [Penjelasan Anotasi JPA](#penjelasan-anotasi-jpa)
3. [Kenapa Terbentuk 3 Tabel](#kenapa-terbentuk-3-tabel)
4. [Alur ORM dari Request sampai Database](#alur-orm-dari-request-sampai-database)
5. [Cara Menjalankan Project](#cara-menjalankan-project)
6. [Testing Endpoint](#testing-endpoint)

---

## Overview Project

Project ini adalah REST API menggunakan:

- **Java 21** - Versi Java terbaru dengan fitur modern
- **Spring Boot 4.0.0** - Framework untuk membangun aplikasi Spring dengan konfigurasi minimal
- **SQL Server 2022** - Database relasional dari Microsoft
- **Hibernate/JPA** - ORM (Object-Relational Mapping) untuk mapping Java object ke tabel database
- **Lombok** - Library untuk mengurangi boilerplate code

### Struktur Package

```
com.gvn.springtutor
├── config/           # Konfigurasi aplikasi
│   └── DataInitializer.java
├── controller/       # REST Controller (presentation layer)
│   ├── RoleController.java
│   └── UserController.java
├── entity/           # JPA Entity (domain model)
│   ├── Role.java
│   └── User.java
├── repository/       # Spring Data Repository (data access layer)
│   ├── RoleRepository.java
│   └── UserRepository.java
├── service/          # Business logic layer
│   ├── RoleService.java
│   └── UserService.java
└── SpringTutorApplication.java
```

---

## Penjelasan Anotasi JPA

### Entity Annotations

| Anotasi                | Fungsi                                                                                                                   |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| `@Entity`              | Menandai class sebagai JPA Entity yang akan di-mapping ke tabel database. Hibernate akan mengelola lifecycle object ini. |
| `@Table(name = "...")` | Menentukan nama tabel di database. Default menggunakan nama class.                                                       |
| `@Id`                  | Menandai field sebagai primary key dari entity.                                                                          |
| `@GeneratedValue`      | Menentukan strategi auto-generation untuk primary key.                                                                   |
| `@Column`              | Konfigurasi detail mapping kolom (nama, constraint, length, dll).                                                        |

### GenerationType Options

| Strategy   | Penjelasan                                                          |
| ---------- | ------------------------------------------------------------------- |
| `IDENTITY` | Menggunakan auto-increment database (cocok untuk SQL Server, MySQL) |
| `SEQUENCE` | Menggunakan database sequence (cocok untuk PostgreSQL, Oracle)      |
| `TABLE`    | Menggunakan tabel khusus untuk generate ID                          |
| `AUTO`     | Hibernate memilih strategi terbaik berdasarkan database             |

### Relationship Annotations

| Anotasi       | Fungsi                                                   |
| ------------- | -------------------------------------------------------- |
| `@ManyToMany` | Mendefinisikan relasi many-to-many antara dua entity.    |
| `@JoinTable`  | Mendefinisikan tabel junction untuk relasi many-to-many. |
| `@JoinColumn` | Mendefinisikan foreign key column dalam relasi.          |

### Fetch Types

| FetchType | Penjelasan                                                                                 |
| --------- | ------------------------------------------------------------------------------------------ |
| `EAGER`   | Data langsung di-load bersamaan dengan entity parent. Berguna jika data selalu dibutuhkan. |
| `LAZY`    | Data di-load hanya saat diakses (on-demand). Default untuk `@ManyToMany` dan `@OneToMany`. |

---

## Kenapa Terbentuk 3 Tabel?

Relasi Many-to-Many antara User dan Role membutuhkan **tabel perantara** (junction/bridge table).

### Ilustrasi Relasi

```
┌─────────────────┐      ┌──────────────────┐      ┌─────────────────┐
│     users       │      │   user_roles     │      │     roles       │
├─────────────────┤      ├──────────────────┤      ├─────────────────┤
│ id (PK)         │──┐   │ user_id (FK)     │   ┌──│ id (PK)         │
│ username        │  └──>│ role_id (FK)     │<──┘  │ name            │
│ email           │      └──────────────────┘      └─────────────────┘
│ password        │
│ is_active       │
└─────────────────┘
```

### Tabel yang Terbentuk

#### 1. `users` - Menyimpan data user

```sql
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150),
    password VARCHAR(255),
    is_active BIT
);
```

#### 2. `roles` - Menyimpan data role

```sql
CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
```

#### 3. `user_roles` - Junction table untuk relasi many-to-many

```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

### Kenapa Perlu Junction Table?

1. **Normalisasi Database** - Tidak mungkin menyimpan multiple foreign key dalam satu kolom
2. **Fleksibilitas** - Satu user bisa punya banyak role, dan satu role bisa dimiliki banyak user
3. **Data Integrity** - Foreign key constraints menjaga konsistensi data
4. **Query Efisien** - Mudah untuk query siapa saja yang punya role tertentu atau role apa saja yang dimiliki user

---

## Alur ORM dari Request sampai Database

### Diagram Alur

```
                          ┌─────────────────────────────────────────────────┐
                          │                  SPRING BOOT                     │
┌──────────┐              │  ┌────────────┐  ┌─────────┐  ┌────────────┐    │  ┌──────────┐
│  CLIENT  │──HTTP REQ───>│  │ CONTROLLER │─>│ SERVICE │─>│ REPOSITORY │    │  │ DATABASE │
│ (Postman)│              │  │            │  │         │  │            │    │  │          │
│          │<──JSON RES───│  │            │<─│         │<─│            │    │  │          │
└──────────┘              │  └────────────┘  └─────────┘  └──────┬─────┘    │  └────┬─────┘
                          │                                      │          │       │
                          │                              ┌───────▼───────┐  │       │
                          │                              │ EntityManager │  │       │
                          │                              │  (Hibernate)  │──┼───────┘
                          │                              └───────────────┘  │
                          └─────────────────────────────────────────────────┘
```

### Contoh Alur: GET /users

#### Step 1: HTTP Request Masuk

```http
GET http://localhost:8080/users
Accept: application/json
```

#### Step 2: Controller Menerima Request

```java
// UserController.java
@GetMapping
public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();  // <-- Panggil service
    return ResponseEntity.ok(users);
}
```

#### Step 3: Service Memproses Business Logic

```java
// UserService.java
public List<User> getAllUsers() {
    return userRepository.findAll();  // <-- Panggil repository
}
```

#### Step 4: Repository Mengakses Data

```java
// JpaRepository.findAll() - method bawaan
// Hibernate akan generate SQL query
```

#### Step 5: Hibernate Generate SQL

```sql
-- Query untuk users
SELECT u.id, u.username, u.email, u.password, u.is_active
FROM users u

-- Query untuk roles (karena FetchType.EAGER)
SELECT ur.user_id, r.id, r.name
FROM user_roles ur
JOIN roles r ON ur.role_id = r.id
WHERE ur.user_id IN (1, 2, 3, ...)
```

#### Step 6: Result Set di-Mapping ke Entity

Hibernate mengkonversi ResultSet dari database menjadi List<User> Java objects, dengan roles sudah terisi.

#### Step 7: JSON Response

```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "isActive": true,
    "roles": [
      { "id": 1, "name": "ADMIN" },
      { "id": 2, "name": "USER" }
    ]
  }
]
```

---

## Cara Menjalankan Project

### Prerequisites

1. Java 21 installed
2. SQL Server 2022 running
3. Database `spring_tutor` sudah dibuat

### Membuat Database

```sql
CREATE DATABASE spring_tutor;
```

### Menjalankan Aplikasi

```bash
# Menggunakan Maven Wrapper
./mvnw spring-boot:run

# Atau jika Maven terinstall global
mvn spring-boot:run
```

### Verifikasi

Akses: http://localhost:8080/users

Hibernate akan otomatis membuat tabel berdasarkan entity.

---

## Testing Endpoint

### 1. Get All Roles

```bash
curl -X GET http://localhost:8080/roles
```

Response:

```json
[
  { "id": 1, "name": "ADMIN" },
  { "id": 2, "name": "USER" }
]
```

### 2. Create New Role

```bash
curl -X POST http://localhost:8080/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "MANAGER"}'
```

Response:

```json
{ "id": 3, "name": "MANAGER" }
```

### 3. Get All Users

```bash
curl -X GET http://localhost:8080/users
```

Response:

```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "isActive": true,
    "roles": [
      { "id": 1, "name": "ADMIN" },
      { "id": 2, "name": "USER" }
    ]
  }
]
```

### 4. Create New User (tanpa role)

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "secret123",
    "isActive": true
  }'
```

### 5. Create New User (dengan role)

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "janedoe",
    "email": "jane@example.com",
    "password": "secret456",
    "isActive": true,
    "roles": [
      {"id": 1},
      {"id": 2}
    ]
  }'
```

### 6. Get Role by ID

```bash
curl -X GET http://localhost:8080/roles/1
```

### 7. Update Role

```bash
curl -X PUT http://localhost:8080/roles/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "SUPER_ADMIN"}'
```

### 8. Delete Role

```bash
curl -X DELETE http://localhost:8080/roles/1
```

### 9. Get User by ID

```bash
curl -X GET http://localhost:8080/users/1
```

### 10. Update User

```bash
curl -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin_updated",
    "email": "admin_new@example.com"
  }'
```

### 11. Delete User

```bash
curl -X DELETE http://localhost:8080/users/1
```

### 12. Get All Products

```bash
curl -X GET http://localhost:8080/products
```

### 13. Create Product

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kredit Multiguna",
    "tenor": 12,
    "interestRate": 10.5
  }'
```

### 14. Get Product by ID

```bash
curl -X GET http://localhost:8080/products/1
```

### 15. Update Product

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Kredit Multiguna Premium",
    "tenor": 18,
    "interestRate": 9.0
  }'
```

### 16. Delete Product

```bash
curl -X DELETE http://localhost:8080/products/1
```

---

## Catatan Penting

### Menghindari Infinite Loop pada Serialisasi

Karena relasi Many-to-Many hanya didefinisikan di sisi User (unidirectional), tidak ada risiko infinite loop saat serialisasi ke JSON.

Jika ingin membuat relasi bidirectional, perlu menambahkan di Role:

```java
@ManyToMany(mappedBy = "roles")
@JsonIgnore  // <-- Mencegah infinite loop
private Set<User> users = new HashSet<>();
```

### DDL-Auto Options

- `update` - Aman untuk development, update schema tanpa hapus data
- `validate` - Untuk production, hanya validasi tanpa mengubah schema
- `create-drop` - Untuk testing, buat ulang schema setiap restart

### Password Security

Contoh ini menyimpan password dalam plain text. Untuk production, gunakan:

- Spring Security untuk authentication
- BCryptPasswordEncoder untuk hash password
