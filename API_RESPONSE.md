# ApiResponse - Dokumentasi

## Apa itu ApiResponse?

`ApiResponse<T>` adalah **Generic Wrapper Class** yang digunakan untuk **standarisasi format response** dari semua REST API endpoint. Class ini membungkus response dalam struktur yang konsisten, sehingga frontend dapat dengan mudah memproses response dari berbagai endpoint.

---

## Mengapa Perlu ApiResponse?

### ❌ Tanpa ApiResponse (Tidak Konsisten)

Setiap endpoint mengembalikan format berbeda:

```json
// GET /users
[{"id": 1, "username": "admin"}]

// POST /users (error)
{"timestamp": "...", "status": 500, "error": "Internal Server Error"}

// GET /products
{"id": 1, "name": "Gold"}
```

Frontend harus handle berbagai format response yang berbeda-beda.

### ✅ Dengan ApiResponse (Konsisten)

Semua endpoint mengembalikan format yang sama:

```json
// GET /users (success)
{
    "success": true,
    "message": "Users retrieved successfully",
    "data": [{"id": 1, "username": "admin"}],
    "code": 200,
    "timestamp": "2025-12-18T06:00:00Z"
}

// POST /users (error)
{
    "success": false,
    "message": "Username already exists",
    "data": null,
    "code": 400,
    "timestamp": "2025-12-18T06:00:00Z"
}
```

Frontend cukup cek `success` untuk menentukan apakah request berhasil atau tidak.

---

## Struktur ApiResponse

```java
public class ApiResponse<T> {
    private Boolean success;    // true = berhasil, false = gagal
    private String message;     // Pesan untuk user/developer
    private T data;             // Data payload (generic)
    private Integer code;       // HTTP status code
    private Instant timestamp;  // Waktu response
}
```

### Field Explanation

| Field       | Tipe        | Deskripsi                                             |
| ----------- | ----------- | ----------------------------------------------------- |
| `success`   | Boolean     | `true` jika request berhasil, `false` jika gagal      |
| `message`   | String      | Pesan informatif (misal: "User created successfully") |
| `data`      | T (Generic) | Data payload - bisa object, list, atau null           |
| `code`      | Integer     | HTTP status code (200, 201, 400, 404, 500, dll)       |
| `timestamp` | Instant     | Waktu response dibuat (ISO 8601 format)               |

---

## Generic Type `<T>`

`ApiResponse` menggunakan **Generic Type** sehingga bisa membungkus berbagai tipe data:

```java
// Membungkus single object
ApiResponse<User> userResponse;

// Membungkus list
ApiResponse<List<Product>> productsResponse;

// Tanpa data (null)
ApiResponse<Void> deleteResponse;
```

---

## Cara Penggunaan

### 1. Di Controller - Success Response

```java
@GetMapping
public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
    List<User> users = userService.getAllUsers();

    ApiResponse<List<User>> response = ApiResponse.success(users, "Users retrieved successfully");

    return ResponseEntity.ok(response);
}
```

**Output:**

```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": [{ "id": 1, "username": "admin", "email": "admin@example.com" }],
  "code": 200,
  "timestamp": "2025-12-18T06:00:00Z"
}
```

### 2. Di Controller - Error Response

```java
@PostMapping
public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
    if (userService.findByUsername(user.getUsername()).isPresent()) {
        ApiResponse<User> response = ApiResponse.error("Username already exists", 400);
        return ResponseEntity.badRequest().body(response);
    }

    User savedUser = userService.createUser(user);
    ApiResponse<User> response = ApiResponse.success(savedUser, "User created successfully");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

**Output (Error):**

```json
{
  "success": false,
  "message": "Username already exists",
  "data": null,
  "code": 400,
  "timestamp": "2025-12-18T06:00:00Z"
}
```

### 3. Menggunakan Builder Pattern

```java
ApiResponse<Product> response = ApiResponse.<Product>builder()
        .success(true)
        .message("Product created")
        .data(product)
        .code(201)
        .timestamp(Instant.now())
        .build();
```

---

## Factory Methods

Class `ApiResponse` menyediakan factory methods untuk mempermudah pembuatan response:

### `ApiResponse.success(data, message)`

```java
// Untuk success response
ApiResponse<User> response = ApiResponse.success(user, "User found");
```

### `ApiResponse.error(message, code)`

```java
// Untuk error response
ApiResponse<User> response = ApiResponse.error("User not found", 404);
```

---

## Contoh Response Format

### Success - Get All Products

```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    { "id": 1, "name": "Bronze", "tenor": 12, "interestRate": 5.0 },
    { "id": 2, "name": "Silver", "tenor": 24, "interestRate": 7.0 },
    { "id": 3, "name": "Gold", "tenor": 36, "interestRate": 9.0 }
  ],
  "code": 200,
  "timestamp": "2025-12-18T06:00:00.000Z"
}
```

### Success - Create User

```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 5,
    "username": "newuser",
    "email": "new@example.com"
  },
  "code": 201,
  "timestamp": "2025-12-18T06:01:00.000Z"
}
```

### Error - Validation Error

```json
{
  "success": false,
  "message": "Username is required",
  "data": null,
  "code": 400,
  "timestamp": "2025-12-18T06:02:00.000Z"
}
```

### Error - Not Found

```json
{
  "success": false,
  "message": "User with id 999 not found",
  "data": null,
  "code": 404,
  "timestamp": "2025-12-18T06:03:00.000Z"
}
```

### Error - Server Error

```json
{
  "success": false,
  "message": "Internal server error occurred",
  "data": null,
  "code": 500,
  "timestamp": "2025-12-18T06:04:00.000Z"
}
```

---

## Keuntungan Menggunakan ApiResponse

| Keuntungan         | Penjelasan                                        |
| ------------------ | ------------------------------------------------- |
| **Konsistensi**    | Semua endpoint memiliki format response yang sama |
| **Mudah di-parse** | Frontend cukup cek field `success` untuk handling |
| **Informatif**     | Field `message` memberikan konteks yang jelas     |
| **Debugging**      | `timestamp` membantu tracking kapan error terjadi |
| **Type-safe**      | Generic type memastikan type safety saat compile  |

---

## Best Practices

1. **Selalu gunakan ApiResponse** untuk semua endpoint
2. **Gunakan message yang informatif** dan user-friendly
3. **Gunakan code yang sesuai** dengan HTTP standard
4. **Jangan expose stack trace** di production (cukup message umum)
5. **Gunakan factory methods** daripada builder untuk kasus sederhana

---

## Integrasi dengan Exception Handler

Project ini sudah memiliki **GlobalExceptionHandler** yang menangani exception secara global:

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .code(HttpStatus.NOT_FOUND.value())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex) {
        // 400 Bad Request
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        // 500 Internal Server Error
    }
}
```

Dengan ini, semua error akan otomatis dibungkus dalam format `ApiResponse` yang konsisten.

> 📖 Untuk dokumentasi lengkap, lihat [ERROR_HANDLING.md](./ERROR_HANDLING.md)
