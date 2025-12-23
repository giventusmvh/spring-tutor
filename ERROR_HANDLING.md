# Error Handling - Dokumentasi

Dokumentasi lengkap untuk implementasi Global Exception Handler di Spring Boot.

---

## 📋 Overview

Project ini menggunakan **Global Exception Handler** untuk menangani semua error secara konsisten. Semua error akan dikembalikan dalam format `ApiResponse` yang standar.

---

## 🏗️ Struktur Exception

```
com.gvn.springtutor.exception
├── BadRequestException.java         # 400 Bad Request
├── ResourceNotFoundException.java   # 404 Not Found
└── GlobalExceptionHandler.java      # Handler semua exception
```

---

## 📦 Exception Classes

### 1. ResourceNotFoundException (404)

Digunakan ketika resource tidak ditemukan di database.

```java
// Cara penggunaan
throw new ResourceNotFoundException("User", "id", 999);
// Output: "User not found with id: '999'"

// Atau dengan custom message
throw new ResourceNotFoundException("Data tidak ditemukan");
```

### 2. BadRequestException (400)

Digunakan ketika request tidak valid.

```java
throw new BadRequestException("Email format tidak valid");
```

---

## 🎯 GlobalExceptionHandler

Handler ini menangani exception berikut:

| Exception                   | HTTP Status               | Deskripsi                |
| --------------------------- | ------------------------- | ------------------------ |
| `ResourceNotFoundException` | 404 Not Found             | Resource tidak ditemukan |
| `BadRequestException`       | 400 Bad Request           | Request tidak valid      |
| `IllegalArgumentException`  | 400 Bad Request           | Argument tidak valid     |
| `RuntimeException`          | 500 Internal Server Error | Error umum runtime       |
| `Exception`                 | 500 Internal Server Error | Error tidak terduga      |

---

## 📤 Response Format

Semua error response menggunakan format `ApiResponse`:

### Success Response

```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": { ... },
  "code": 200,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

### Error Response (404 Not Found)

```json
{
  "success": false,
  "message": "User not found with id: '999'",
  "data": null,
  "code": 404,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

### Error Response (400 Bad Request)

```json
{
  "success": false,
  "message": "Email format tidak valid",
  "data": null,
  "code": 400,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

### Error Response (500 Internal Server Error)

```json
{
  "success": false,
  "message": "An unexpected error occurred: ...",
  "data": null,
  "code": 500,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

---

## 🧪 Contoh Testing

### Test 404 - Resource Not Found

```bash
# User tidak ada
curl http://localhost:8081/users/9999

# Response:
{
  "success": false,
  "message": "User not found with id: '9999'",
  "data": null,
  "code": 404,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

```bash
# Product tidak ada
curl http://localhost:8081/products/9999

# Response:
{
  "success": false,
  "message": "Product not found with id: '9999'",
  "data": null,
  "code": 404,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

```bash
# Role tidak ada
curl http://localhost:8081/roles/9999

# Response:
{
  "success": false,
  "message": "Role not found with id: '9999'",
  "data": null,
  "code": 404,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

---

## 💻 Implementasi di Service

Contoh penggunaan di Service layer:

```java
@Service
public class UserService {

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Update logic...
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(existingUser);
    }
}
```

---

## 🔧 Cara Menambah Exception Baru

1. **Buat Exception Class baru:**

```java
package com.gvn.springtutor.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
```

2. **Tambahkan handler di GlobalExceptionHandler:**

```java
@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException ex) {
    log.error("Unauthorized: {}", ex.getMessage());

    ApiResponse<Object> response = ApiResponse.builder()
            .success(false)
            .message(ex.getMessage())
            .data(null)
            .code(HttpStatus.UNAUTHORIZED.value())
            .timestamp(Instant.now())
            .build();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
}
```

---

## 📝 Best Practices

1. **Gunakan exception yang tepat** - Jangan gunakan `RuntimeException` untuk semua kasus
2. **Pesan yang jelas** - Berikan pesan error yang informatif
3. **Log error** - Semua error di-log untuk debugging
4. **Konsisten** - Semua response menggunakan format yang sama
5. **Jangan expose internal details** - Hindari menampilkan stack trace ke user

---

## 🔗 Related Documentation

- [API_POSTMAN.md](./API_POSTMAN.md) - Dokumentasi API endpoint
- [API_RESPONSE.md](./API_RESPONSE.md) - Format response standar
- [RESPONSE_UTIL.md](./RESPONSE_UTIL.md) - Utility untuk response
