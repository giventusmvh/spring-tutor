# JWT Authentication - Dokumentasi

## Overview

Implementasi JWT (JSON Web Token) authentication untuk REST API menggunakan Spring Security 6.

### Flow Authentication

```
[1. Register/Login] → [2. Server Validate] → [3. Generate JWT] → [4. Return Token]
                                                                          ↓
[5. API Request + Bearer Token] → [6. Validate Token] → [7. Access Resource]
```

---

## Endpoints

### Public Endpoints (Tanpa Authentication)

| Method | Endpoint         | Description                  |
| ------ | ---------------- | ---------------------------- |
| POST   | `/auth/login`    | Login dan dapatkan JWT token |
| POST   | `/auth/register` | Register user baru           |

### Protected Endpoints (Membutuhkan JWT Token)

| Method | Endpoint    | Description      |
| ------ | ----------- | ---------------- |
| GET    | `/users`    | Get all users    |
| POST   | `/users`    | Create user      |
| GET    | `/roles`    | Get all roles    |
| POST   | `/roles`    | Create role      |
| GET    | `/products` | Get all products |
| POST   | `/products` | Create product   |

---

## Penggunaan

### 1. Register User Baru

**Request:**

```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "newuser@example.com",
    "password": "password123"
  }'
```

**Response:**

```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "username": "newuser",
    "roles": ["USER"]
  },
  "code": 201,
  "timestamp": "2025-12-22T01:52:14.819865Z"
}
```

---

### 2. Login

**Request:**

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123"
  }'
```

**Response:**

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "username": "newuser",
    "roles": ["USER"]
  },
  "code": 200,
  "timestamp": "2025-12-22T01:52:39.693242Z"
}
```

---

### 3. Akses Protected Endpoint

**Tanpa Token (401/403):**

```bash
curl http://localhost:8081/users
```

Response:

```json
{ "timestamp": "...", "status": 403, "error": "Forbidden", "path": "/users" }
```

**Dengan Token (200 OK):**

```bash
curl http://localhost:8081/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

Response:

```json
{
    "success": true,
    "message": "Users retrieved successfully",
    "data": [...],
    "code": 200,
    "timestamp": "..."
}
```

---

## Struktur JWT Token

Token JWT terdiri dari 3 bagian yang dipisahkan oleh titik (`.`):

```
HEADER.PAYLOAD.SIGNATURE
```

### Header

```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

### Payload

```json
{
  "roles": [{ "authority": "ROLE_USER" }],
  "sub": "username",
  "iat": 1766368334,
  "exp": 1766454734
}
```

| Claim   | Description                      |
| ------- | -------------------------------- |
| `roles` | Daftar role user                 |
| `sub`   | Subject (username)               |
| `iat`   | Issued at (waktu token dibuat)   |
| `exp`   | Expiration (waktu token expired) |

---

## File Structure

```
security/
├── JwtUtil.java              # Generate, validate, extract token
├── JwtAuthenticationFilter.java  # Filter untuk validasi token
├── CustomUserDetailsService.java # Load user dari DB
└── SecurityConfig.java       # Spring Security configuration

dto/
├── AuthRequest.java          # Login request DTO
├── AuthResponse.java         # Login response dengan token
└── RegisterRequest.java      # Register request DTO

service/
└── AuthService.java          # Login & register logic

controller/
└── AuthController.java       # /auth endpoints
```

---

## Konfigurasi

### application.yml

```yaml
jwt:
  secret: mySecretKeyForJWTTokenGenerationWhichShouldBeLongEnoughForHS256Algorithm2024
  expiration: 86400000 # 24 hours in milliseconds
```

---

## Security Flow

```mermaid
sequenceDiagram
    participant Client
    participant Filter as JwtAuthFilter
    participant Security as SecurityContext
    participant Controller
    participant Service

    Client->>Filter: Request + Bearer Token
    Filter->>Filter: Extract token from header
    Filter->>Filter: Validate token (JwtUtil)
    Filter->>Security: Set Authentication
    Security->>Controller: Forward request
    Controller->>Service: Business logic
    Service-->>Client: Response
```

---

## Error Responses

### 401 Unauthorized

Token tidak valid atau expired.

### 403 Forbidden

Request tanpa token atau user tidak memiliki akses.

### 400 Bad Request

- Username sudah ada (register)
- Invalid request body

---

## Best Practices

1. **Jangan expose password** - Gunakan DTO yang berbeda untuk response
2. **Token expiration** - Set expiration yang sesuai (default 24 jam)
3. **Secret key** - Gunakan secret key yang panjang dan aman
4. **HTTPS** - Selalu gunakan HTTPS di production
5. **Refresh token** - Implementasikan refresh token untuk UX yang lebih baik
