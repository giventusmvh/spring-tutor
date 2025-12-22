# Tutorial: Redis Caching di Spring Boot

Dokumentasi lengkap implementasi Redis caching.

---

## Daftar Isi

1. [Pendahuluan](#pendahuluan)
2. [Setup Dependencies](#1-setup-dependencies)
3. [Konfigurasi Redis](#2-konfigurasi-redis)
4. [RedisConfig Class](#3-redisconfig-class)
5. [Entity Serializable](#4-entity-serializable)
6. [Caching Annotations](#5-caching-annotations)
7. [Testing](#6-testing)

---

## Pendahuluan

### Apa itu Redis?

**Redis** (Remote Dictionary Server) adalah in-memory data store yang sangat cepat. Digunakan untuk:

- **Caching** - Menyimpan hasil query agar tidak query database berulang
- **Session storage** - Menyimpan session user
- **Message queue** - Pub/Sub messaging

### Kenapa Cache?

```
TANPA CACHE:
Request → Service → Database → Response (100ms)
Request → Service → Database → Response (100ms)
Request → Service → Database → Response (100ms)

DENGAN CACHE:
Request → Service → Database → Cache → Response (100ms)  ← First call
Request → Cache → Response (1ms)                         ← Cache HIT!
Request → Cache → Response (1ms)                         ← Cache HIT!
```

---

## 1. Setup Dependencies

### pom.xml

```xml
<!-- Redis Cache -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### Penjelasan:

- **spring-boot-starter-data-redis** - Library untuk koneksi ke Redis
- **spring-boot-starter-cache** - Abstraksi caching Spring

---

## 2. Konfigurasi Redis

### application.yml

```yaml
# ============================================
# REDIS CONFIGURATION
# ============================================
spring.data:
  redis:
    host: localhost
    port: 6379
    # password: yourpassword  # Uncomment jika Redis membutuhkan password
    timeout: 60000 # Connection timeout in ms
    lettuce:
      pool:
        max-active: 8 # Max koneksi aktif
        max-idle: 8 # Max koneksi idle
        min-idle: 0 # Min koneksi idle

# ============================================
# CACHE CONFIGURATION
# ============================================
spring.cache:
  type: redis
  redis:
    time-to-live: 3600000 # 1 hour in ms
    cache-null-values: false
```

### Penjelasan:

| Property       | Keterangan                |
| -------------- | ------------------------- |
| `host`         | Hostname Redis server     |
| `port`         | Port Redis (default 6379) |
| `timeout`      | Connection timeout        |
| `max-active`   | Max koneksi bersamaan     |
| `time-to-live` | Berapa lama data di-cache |

---

## 3. RedisConfig Class

### File: `config/RedisConfig.java`

```java
package com.gvn.springtutor.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching  // ← Mengaktifkan caching
public class RedisConfig {

    /**
     * Redis Template untuk operasi manual.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key menggunakan String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value menggunakan JDK Serialization (entities implement Serializable)
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());

        return template;
    }

    /**
     * Cache Manager dengan konfigurasi.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))                    // TTL 1 jam
                .disableCachingNullValues()                        // Jangan cache null
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new JdkSerializationRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
```

> **Note**: Menggunakan `JdkSerializationRedisSerializer` karena `GenericJackson2JsonRedisSerializer` deprecated di Spring Data Redis 4.0. Semua entity harus implements `Serializable`.

### Penjelasan:

- **@EnableCaching** - Mengaktifkan fitur caching Spring
- **RedisTemplate** - Untuk operasi Redis manual (set/get langsung)
- **RedisCacheManager** - Manager untuk annotation-based caching

---

## 4. Entity Serializable

Entity harus implement `Serializable` agar bisa di-cache:

```java
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "products")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ... fields
}
```

### Kenapa Serializable?

- Redis menyimpan data dalam format binary/serialized
- Java object harus bisa di-convert ke bytes dan sebaliknya
- `serialVersionUID` memastikan konsistensi saat deserialization

---

## 5. Caching Annotations

### @Cacheable - Cache hasil method

```java
@Cacheable(value = "products", key = "'allProducts'")
public List<Product> getAllProducts() {
    log.info("Fetching from DATABASE");  // Hanya muncul saat cache miss
    return productRepository.findAll();
}
```

**Behavior:**

1. First call → Query database → Simpan ke cache → Return
2. Next calls → Return dari cache (tidak query database)

### @CacheEvict - Hapus cache

```java
@CacheEvict(value = "products", allEntries = true)
public Product createProduct(Product product) {
    return productRepository.save(product);
}
```

**Behavior:**

- Setelah create product, cache "products" di-clear
- Call berikutnya akan query database lagi (data fresh)

### @CachePut - Update cache

```java
@CachePut(value = "products", key = "#product.id")
public Product updateProduct(Product product) {
    return productRepository.save(product);
}
```

**Behavior:**

- Selalu eksekusi method
- Update cache dengan hasil baru

---

## 6. Testing

### Verifikasi Caching

```bash
# Login dan dapatkan token
TOKEN=$(curl -s -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' | \
  python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")

# First call - akan query database
curl http://localhost:8081/products -H "Authorization: Bearer $TOKEN"

# Second call - dari cache (tidak query database)
curl http://localhost:8081/products -H "Authorization: Bearer $TOKEN"
```

### Cek Log

**First call (Cache MISS):**

```
INFO  ProductService : Fetching all products from DATABASE
Hibernate: select p1_0.id, ... from products p1_0
```

**Second call (Cache HIT):**

```
(tidak ada log - data dari cache)
```

### Lihat Data di Redis

```bash
# Masuk ke Redis CLI
docker exec -it redis redis-cli

# Lihat semua keys
KEYS *

# Lihat isi cache
GET "products::allProducts"
```

---

## Summary

| Annotation       | Fungsi             |
| ---------------- | ------------------ |
| `@EnableCaching` | Aktifkan caching   |
| `@Cacheable`     | Cache hasil method |
| `@CacheEvict`    | Hapus cache        |
| `@CachePut`      | Update cache       |

### Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        GET /products                             │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│              Check Redis Cache (key: "allProducts")             │
└─────────────────────────────────────────────────────────────────┘
                               │
              ┌────────────────┴────────────────┐
              ▼                                 ▼
┌─────────────────────────┐       ┌─────────────────────────┐
│     CACHE HIT           │       │     CACHE MISS          │
│   Return dari Redis     │       │   Query Database        │
│   (< 1ms)               │       │   Save ke Redis         │
└─────────────────────────┘       │   Return Response       │
                                  └─────────────────────────┘
```

---

## Best Practices

1. **TTL yang tepat** - Jangan terlalu lama (data stale) atau terlalu pendek (tidak efektif)
2. **Clear cache saat data berubah** - Gunakan @CacheEvict di create/update/delete
3. **Key yang unique** - Pastikan key cache tidak bentrok
4. **Monitor cache** - Pantau hit/miss ratio
5. **Jangan cache semua** - Hanya cache data yang sering diakses dan jarang berubah
