# ResponseUtil - Dokumentasi

## Apa itu ResponseUtil?

`ResponseUtil` adalah **Utility Class** yang menyediakan **static helper methods** untuk membuat standar `ApiResponse` dengan lebih mudah dan konsisten. Class ini membantu mengurangi boilerplate code di controller.

---

## Perbandingan

### ❌ Tanpa ResponseUtil (Verbose)

```java
@GetMapping
public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
    List<User> users = userService.getAllUsers();

    ApiResponse<List<User>> response = ApiResponse.<List<User>>builder()
            .success(true)
            .message("Users retrieved successfully")
            .data(users)
            .code(200)
            .timestamp(Instant.now())
            .build();

    return ResponseEntity.ok(response);
}
```

### ✅ Dengan ResponseUtil (Clean)

```java
@GetMapping
public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseUtil.ok(users, "Users retrieved successfully");
}
```

---

## Methods yang Tersedia

### 1. `ResponseUtil.ok(data, message)`

Untuk **GET request** yang berhasil (HTTP 200).

```java
ResponseUtil.ok(users, "Users retrieved successfully")
```

**Output:**

```json
{
    "success": true,
    "message": "Users retrieved successfully",
    "data": [...],
    "code": 200,
    "timestamp": "2025-12-18T06:00:00Z"
}
```

---

### 2. `ResponseUtil.created(data, message)`

Untuk **POST request** yang berhasil membuat resource baru (HTTP 201).

```java
ResponseUtil.created(savedUser, "User created successfully")
```

**Output:**

```json
{
  "success": true,
  "message": "User created successfully",
  "data": { "id": 1, "username": "admin" },
  "code": 201,
  "timestamp": "2025-12-18T06:00:00Z"
}
```

---

### 3. `ResponseUtil.error(status, message)`

Untuk **error response** dengan HTTP status tertentu.

```java
ResponseUtil.error(HttpStatus.NOT_FOUND, "User not found")
ResponseUtil.error(HttpStatus.BAD_REQUEST, "Invalid input")
ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "Server error")
```

**Output:**

```json
{
  "success": false,
  "message": "User not found",
  "data": null,
  "code": 404,
  "timestamp": "2025-12-18T06:00:00Z"
}
```

---

## Source Code

```java
package com.gvn.springtutor.util;

import com.gvn.springtutor.base.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;

public class ResponseUtil {

    // Success response dengan HTTP 200
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        ApiResponse<T> body = ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .code(HttpStatus.OK.value())
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.ok(body);
    }

    // Success response dengan HTTP 201 (Created)
    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        ApiResponse<T> body = ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .code(HttpStatus.CREATED.value())
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // Error response dengan custom HTTP status
    public static ResponseEntity<ApiResponse<Object>> error(HttpStatus status, String message) {
        ApiResponse<Object> body = ApiResponse.builder()
            .success(false)
            .message(message)
            .data(null)
            .code(status.value())
            .timestamp(Instant.now())
            .build();
        return ResponseEntity.status(status).body(body);
    }
}
```

---

## Contoh Implementasi di Controller

### UserController

```java
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseUtil.ok(users, "Users retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseUtil.created(savedUser, "User created successfully");
    }
}
```

### RoleController

```java
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseUtil.ok(roles, "Roles retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
        Role savedRole = roleService.createRole(role);
        return ResponseUtil.created(savedRole, "Role created successfully");
    }
}
```

### ProductController

```java
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseUtil.ok(products, "Products retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.createProduct(product);
        return ResponseUtil.created(savedProduct, "Product created successfully");
    }
}
```

---

## Keuntungan Menggunakan ResponseUtil

| Keuntungan           | Penjelasan                                            |
| -------------------- | ----------------------------------------------------- |
| **Less Boilerplate** | Tidak perlu build ApiResponse manual di setiap method |
| **Konsisten**        | Semua response pasti menggunakan format yang sama     |
| **Readable**         | Code lebih bersih dan mudah dibaca                    |
| **Maintainable**     | Jika format berubah, cukup edit di satu tempat        |
| **Type-safe**        | Generic method memastikan type safety                 |

---

## Best Practices

1. **Gunakan `ok()` untuk GET** - ambil data yang sudah ada
2. **Gunakan `created()` untuk POST** - buat resource baru
3. **Gunakan `error()` untuk semua error** - dengan HttpStatus yang sesuai
4. **Message harus informatif** - jelaskan apa yang terjadi
5. **Jangan hardcode status code** - gunakan `HttpStatus` enum
