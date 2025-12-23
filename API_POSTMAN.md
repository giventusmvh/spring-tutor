# API Documentation - Postman Guide

Dokumentasi lengkap untuk testing REST API menggunakan Postman.

> **Base URL**: `http://localhost:8081`

---

## 📋 Daftar Endpoint

### Role Endpoints

| Method | Endpoint      | Deskripsi                     |
| ------ | ------------- | ----------------------------- |
| GET    | `/roles`      | Mengambil semua role          |
| POST   | `/roles`      | Membuat role baru             |
| GET    | `/roles/{id}` | Mengambil role berdasarkan ID |
| PUT    | `/roles/{id}` | Update role berdasarkan ID    |
| DELETE | `/roles/{id}` | Hapus role berdasarkan ID     |

### User Endpoints

| Method | Endpoint      | Deskripsi                         |
| ------ | ------------- | --------------------------------- |
| GET    | `/users`      | Mengambil semua user beserta role |
| POST   | `/users`      | Membuat user baru                 |
| GET    | `/users/{id}` | Mengambil user berdasarkan ID     |
| PUT    | `/users/{id}` | Update user berdasarkan ID        |
| DELETE | `/users/{id}` | Hapus user berdasarkan ID         |

### Product Endpoints

| Method | Endpoint         | Deskripsi                        |
| ------ | ---------------- | -------------------------------- |
| GET    | `/products`      | Mengambil semua product          |
| POST   | `/products`      | Membuat product baru             |
| GET    | `/products/{id}` | Mengambil product berdasarkan ID |
| PUT    | `/products/{id}` | Update product berdasarkan ID    |
| DELETE | `/products/{id}` | Hapus product berdasarkan ID     |

---

## 🔐 Role Endpoints

### 1. Get All Roles

Mengambil daftar semua role yang tersedia.

**Request:**

```
GET http://localhost:8081/roles
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "name": "ADMIN"
  },
  {
    "id": 2,
    "name": "USER"
  }
]
```

---

### 2. Create New Role

Membuat role baru.

**Request:**

```
POST http://localhost:8081/roles
```

**Headers:**
| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Accept | application/json |

**Body (raw JSON):**

```json
{
  "name": "MANAGER"
}
```

**Response:** `201 Created`

```json
{
  "id": 3,
  "name": "MANAGER"
}
```

**Possible Errors:**
| Status | Deskripsi |
|--------|-----------|
| 500 | Role dengan nama tersebut sudah ada (duplicate) |

---

### 3. Get Role by ID

Mengambil role berdasarkan ID.

**Request:**

```
GET http://localhost:8081/roles/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Role retrieved successfully",
  "data": {
    "id": 1,
    "name": "ADMIN"
  }
}
```

**Possible Errors:**
| Status | Deskripsi |
|--------|-----------|
| 500 | Role tidak ditemukan |

---

### 4. Update Role

Update role berdasarkan ID.

**Request:**

```
PUT http://localhost:8081/roles/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Accept | application/json |

**Body (raw JSON):**

```json
{
  "name": "SUPER_ADMIN"
}
```

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Role updated successfully",
  "data": {
    "id": 1,
    "name": "SUPER_ADMIN"
  }
}
```

---

### 5. Delete Role

Hapus role berdasarkan ID.

**Request:**

```
DELETE http://localhost:8081/roles/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Role deleted successfully",
  "data": null
}
```

---

## 👤 User Endpoints

### 6. Get All Users

Mengambil daftar semua user beserta role yang dimiliki.

**Request:**

```
GET http://localhost:8081/users
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "isActive": true,
    "roles": [
      {
        "id": 1,
        "name": "ADMIN"
      },
      {
        "id": 2,
        "name": "USER"
      }
    ]
  },
  {
    "id": 2,
    "username": "johndoe",
    "email": "john@example.com",
    "password": "secret123",
    "isActive": true,
    "roles": [
      {
        "id": 2,
        "name": "USER"
      },
      {
        "id": 3,
        "name": "MANAGER"
      }
    ]
  }
]
```

---

### 7. Create New User (Tanpa Role)

Membuat user baru tanpa assign role.

**Request:**

```
POST http://localhost:8081/users
```

**Headers:**
| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Accept | application/json |

**Body (raw JSON):**

```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "secret123",
  "isActive": true
}
```

**Response:** `201 Created`

```json
{
  "id": 2,
  "username": "johndoe",
  "email": "john@example.com",
  "password": "secret123",
  "isActive": true,
  "roles": []
}
```

---

### 8. Create New User (Dengan Role)

Membuat user baru dan langsung assign role yang sudah ada.

**Request:**

```
POST http://localhost:8081/users
```

**Headers:**
| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Accept | application/json |

**Body (raw JSON):**

```json
{
  "username": "janedoe",
  "email": "jane@example.com",
  "password": "password456",
  "isActive": true,
  "roles": [{ "id": 1 }, { "id": 2 }]
}
```

> ⚠️ **Penting**: Untuk assign role, cukup kirim `id` role yang sudah ada di database.

**Response:** `201 Created`

```json
{
  "id": 3,
  "username": "janedoe",
  "email": "jane@example.com",
  "password": "password456",
  "isActive": true,
  "roles": [
    {
      "id": 1,
      "name": "ADMIN"
    },
    {
      "id": 2,
      "name": "USER"
    }
  ]
}
```

**Possible Errors:**
| Status | Deskripsi |
|--------|-----------|
| 500 | Username sudah ada (duplicate) |

---

### 9. Get User by ID

Mengambil user berdasarkan ID.

**Request:**

```
GET http://localhost:8081/users/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "roles": [{ "id": 1, "name": "ADMIN" }]
  }
}
```

---

### 10. Update User

Update user berdasarkan ID.

**Request:**

```
PUT http://localhost:8081/users/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Accept | application/json |

**Body (raw JSON):**

```json
{
  "username": "admin_updated",
  "email": "admin_new@example.com"
}
```

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "id": 1,
    "username": "admin_updated",
    "email": "admin_new@example.com"
  }
}
```

---

### 11. Delete User

Hapus user berdasarkan ID.

**Request:**

```
DELETE http://localhost:8081/users/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": null
}
```

---

## 📦 Product Endpoints

### 12. Get All Products

Mengambil semua product.

**Request:**

```
GET http://localhost:8081/products
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Kredit Multiguna",
      "tenor": 12,
      "interestRate": 10.5
    }
  ]
}
```

---

### 13. Create Product

Membuat product baru menggunakan `CreateProductRequest` DTO dengan validasi.

**Request:**

```
POST http://localhost:8081/products
```

**Headers:**
| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Accept | application/json |

**Body (raw JSON):**

```json
{
  "name": "Kredit Kendaraan",
  "tenor": 24,
  "interestRate": 8.5
}
```

**Request DTO: `CreateProductRequest`**

| Field        | Tipe    | Required | Validation                      |
| ------------ | ------- | -------- | ------------------------------- |
| name         | String  | ✅ Ya    | @NotBlank - tidak boleh kosong  |
| tenor        | Integer | ✅ Ya    | @NotNull, @Positive - harus > 0 |
| interestRate | Double  | ✅ Ya    | @NotNull, @Positive - harus > 0 |

**Response:** `201 Created`

```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": 2,
    "name": "Kredit Kendaraan",
    "tenor": 24,
    "interestRate": 8.5
  }
}
```

**Validation Error Response:** `400 Bad Request`

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "tenor": "Tenor must be positive"
  }
}
```

---

### 14. Get Product by ID

Mengambil product berdasarkan ID.

**Request:**

```
GET http://localhost:8081/products/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Product retrieved successfully",
  "data": {
    "id": 1,
    "name": "Kredit Multiguna",
    "tenor": 12,
    "interestRate": 10.5
  }
}
```

---

### 15. Update Product

Update product berdasarkan ID menggunakan `UpdateProductRequest` DTO dengan partial update.

**Request:**

```
PUT http://localhost:8081/products/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Content-Type | application/json |
| Accept | application/json |

**Body (raw JSON):**

```json
{
  "name": "Kredit Multiguna Premium",
  "tenor": 18,
  "interestRate": 9.0
}
```

**Request DTO: `UpdateProductRequest`**

| Field        | Tipe    | Required | Validation                       |
| ------------ | ------- | -------- | -------------------------------- |
| name         | String  | Tidak    | Optional - partial update        |
| tenor        | Integer | Tidak    | @Positive - jika diisi harus > 0 |
| interestRate | Double  | Tidak    | @Positive - jika diisi harus > 0 |

> **Note:** Ini adalah partial update - hanya field yang diisi yang akan diupdate. Field yang tidak diisi akan tetap menggunakan nilai sebelumnya.

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Product updated successfully",
  "data": {
    "id": 1,
    "name": "Kredit Multiguna Premium",
    "tenor": 18,
    "interestRate": 9.0
  }
}
```

---

### 16. Delete Product

Hapus product berdasarkan ID.

**Request:**

```
DELETE http://localhost:8081/products/1
```

**Headers:**
| Key | Value |
|-----|-------|
| Accept | application/json |

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Product deleted successfully",
  "data": null
}
```

---

## 🧪 Postman Collection Setup

### Import Collection

1. Buka Postman
2. Klik **Import** → **Raw text**
3. Paste JSON collection di bawah ini

### Postman Collection JSON

```json
{
  "info": {
    "name": "Spring Tutor API",
    "description": "REST API untuk User & Role Management",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8081"
    }
  ],
  "item": [
    {
      "name": "Roles",
      "item": [
        {
          "name": "Get All Roles",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/roles",
            "header": [
              {
                "key": "Accept",
                "value": "application/json"
              }
            ]
          }
        },
        {
          "name": "Create Role",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/roles",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"name\": \"MANAGER\"\n}"
            }
          }
        }
      ]
    },
    {
      "name": "Users",
      "item": [
        {
          "name": "Get All Users",
          "request": {
            "method": "GET",
            "url": "{{baseUrl}}/users",
            "header": [
              {
                "key": "Accept",
                "value": "application/json"
              }
            ]
          }
        },
        {
          "name": "Create User (No Role)",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/users",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"username\": \"johndoe\",\n    \"email\": \"john@example.com\",\n    \"password\": \"secret123\",\n    \"isActive\": true\n}"
            }
          }
        },
        {
          "name": "Create User (With Roles)",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/users",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"username\": \"janedoe\",\n    \"email\": \"jane@example.com\",\n    \"password\": \"password456\",\n    \"isActive\": true,\n    \"roles\": [\n        {\"id\": 1},\n        {\"id\": 2}\n    ]\n}"
            }
          }
        }
      ]
    }
  ]
}
```

---

## 📝 Field Descriptions

### User Fields

| Field    | Tipe    | Required | Deskripsi                            |
| -------- | ------- | -------- | ------------------------------------ |
| username | String  | ✅ Ya    | Username unik, maksimal 100 karakter |
| email    | String  | Tidak    | Alamat email, maksimal 150 karakter  |
| password | String  | Tidak    | Password (plain text untuk demo)     |
| isActive | Boolean | Tidak    | Status aktif user                    |
| roles    | Array   | Tidak    | Array of role objects dengan id      |

### Role Fields

| Field | Tipe   | Required | Deskripsi                            |
| ----- | ------ | -------- | ------------------------------------ |
| name  | String | ✅ Ya    | Nama role unik, maksimal 50 karakter |

### Product Fields

#### CreateProductRequest (POST /products)

| Field        | Tipe    | Required | Validation                      |
| ------------ | ------- | -------- | ------------------------------- |
| name         | String  | ✅ Ya    | @NotBlank - tidak boleh kosong  |
| tenor        | Integer | ✅ Ya    | @NotNull, @Positive - harus > 0 |
| interestRate | Double  | ✅ Ya    | @NotNull, @Positive - harus > 0 |

#### UpdateProductRequest (PUT /products/{id})

| Field        | Tipe    | Required | Validation                       |
| ------------ | ------- | -------- | -------------------------------- |
| name         | String  | Tidak    | Optional - partial update        |
| tenor        | Integer | Tidak    | @Positive - jika diisi harus > 0 |
| interestRate | Double  | Tidak    | @Positive - jika diisi harus > 0 |

> **Partial Update**: Hanya field yang diisi yang akan diupdate. Field yang tidak diisi/null akan tetap menggunakan nilai sebelumnya.

---

## 🚀 Quick Test dengan cURL

### Get All Roles

```bash
curl -X GET http://localhost:8081/roles
```

### Create Role

```bash
curl -X POST http://localhost:8081/roles \
  -H "Content-Type: application/json" \
  -d '{"name": "SUPERVISOR"}'
```

### Get All Users

```bash
curl -X GET http://localhost:8081/users
```

### Create User with Roles

```bash
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "new@example.com",
    "password": "pass123",
    "isActive": true,
    "roles": [{"id": 1}]
  }'
```

---

## ⚙️ Environment Variables (Postman)

Buat environment baru dengan variabel:

| Variable | Initial Value         | Current Value         |
| -------- | --------------------- | --------------------- |
| baseUrl  | http://localhost:8081 | http://localhost:8081 |

Dengan environment variables, Anda bisa dengan mudah switch antara development dan production URL.

---

## ❌ Error Responses

Semua error response menggunakan format `ApiResponse` yang konsisten.

### 404 - Resource Not Found

Ketika resource tidak ditemukan di database.

```bash
curl http://localhost:8081/users/9999
```

**Response:** `404 Not Found`

```json
{
  "success": false,
  "message": "User not found with id: '9999'",
  "data": null,
  "code": 404,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

### 400 - Bad Request

Ketika request tidak valid.

**Response:** `400 Bad Request`

```json
{
  "success": false,
  "message": "Invalid request parameters",
  "data": null,
  "code": 400,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

### 500 - Internal Server Error

Ketika terjadi error pada server.

**Response:** `500 Internal Server Error`

```json
{
  "success": false,
  "message": "An unexpected error occurred: ...",
  "data": null,
  "code": 500,
  "timestamp": "2025-12-23T04:37:40Z"
}
```

> 📖 Untuk dokumentasi lengkap tentang error handling, lihat [ERROR_HANDLING.md](./ERROR_HANDLING.md)
