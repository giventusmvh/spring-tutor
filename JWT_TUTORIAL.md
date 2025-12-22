# Tutorial: Membuat JWT Authentication di Spring Boot

Dokumentasi lengkap cara membuat JWT Authentication dari awal sampai akhir.

---

## Daftar Isi

1. [Pendahuluan](#pendahuluan)
2. [Dependencies](#1-dependencies)
3. [Konfigurasi JWT](#2-konfigurasi-jwt)
4. [JwtUtil - Token Generator & Validator](#3-jwtutil---token-generator--validator)
5. [CustomUserDetailsService](#4-customuserdetailsservice)
6. [JwtAuthenticationFilter](#5-jwtauthenticationfilter)
7. [SecurityConfig](#6-securityconfig)
8. [DTOs](#7-dtos)
9. [AuthService](#8-authservice)
10. [AuthController](#9-authcontroller)
11. [Testing](#10-testing)

---

## Pendahuluan

### Apa itu JWT?

**JWT (JSON Web Token)** adalah standar open (RFC 7519) untuk membuat token yang berisi claims (informasi). Token ini digunakan untuk authentication dan information exchange.

### Kenapa JWT?

| Keuntungan          | Penjelasan                                                 |
| ------------------- | ---------------------------------------------------------- |
| **Stateless**       | Server tidak perlu simpan session, token berisi semua info |
| **Scalable**        | Mudah scale horizontally karena tidak ada session          |
| **Cross-domain**    | Bisa digunakan di microservices                            |
| **Mobile-friendly** | Cocok untuk mobile dan SPA                                 |

### Struktur JWT

```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjk5...
└──────── Header ────────┘.└────────── Payload ──────────┘.└─ Signature ─┘
```

1. **Header** - Algorithm & token type
2. **Payload** - Data/claims (username, roles, expiration)
3. **Signature** - Verifikasi token tidak diubah

---

## 1. Dependencies

### pom.xml

Tambahkan dependencies berikut:

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JJWT Library) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### Penjelasan:

- **spring-boot-starter-security** - Framework security dari Spring
- **jjwt-api** - API untuk JWT operations
- **jjwt-impl** - Implementasi JJWT (runtime)
- **jjwt-jackson** - Jackson databind untuk serialization (runtime)

---

## 2. Konfigurasi JWT

### application.yml

```yaml
jwt:
  # Secret key untuk signing JWT
  # PENTING: Gunakan key yang panjang (minimal 256 bit untuk HS256)
  secret: mySecretKeyForJWTTokenGenerationWhichShouldBeLongEnoughForHS256Algorithm2024

  # Token expiration dalam milliseconds
  # 86400000 ms = 24 jam
  expiration: 86400000
```

### Penjelasan:

- **secret** - Key rahasia untuk sign token. Jika key ini bocor, attacker bisa buat token valid
- **expiration** - Berapa lama token valid. Setelah expired, user harus login ulang

---

## 3. JwtUtil - Token Generator & Validator

### File: `security/JwtUtil.java`

```java
package com.gvn.springtutor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Generate SecretKey dari string secret.
     * Menggunakan HMAC-SHA untuk signing.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract username dari token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date dari token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract claim tertentu dari token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract semua claims dari token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check apakah token sudah expired.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generate token untuk user.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Create token dengan claims dan subject.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)                                    // Data tambahan (roles)
                .subject(subject)                                  // Username
                .issuedAt(new Date(System.currentTimeMillis()))    // Waktu dibuat
                .expiration(new Date(System.currentTimeMillis() + expiration))  // Waktu expired
                .signWith(getSigningKey())                         // Signing dengan secret key
                .compact();                                        // Build jadi string
    }

    /**
     * Validate token.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

### Penjelasan Method:

| Method                | Fungsi                                        |
| --------------------- | --------------------------------------------- |
| `getSigningKey()`     | Convert string secret ke SecretKey object     |
| `extractUsername()`   | Ambil username dari token                     |
| `extractExpiration()` | Ambil waktu expired dari token                |
| `extractAllClaims()`  | Parse token dan ambil semua data              |
| `isTokenExpired()`    | Cek apakah token sudah expired                |
| `generateToken()`     | Buat token baru untuk user                    |
| `createToken()`       | Build JWT dengan claims, subject, dates       |
| `validateToken()`     | Validasi token (username match & not expired) |

### Flow Generate Token:

```
UserDetails → Extract username & roles → Build claims → Sign with secret → JWT String
```

### Flow Validate Token:

```
JWT String → Parse & verify signature → Extract claims → Check expiration → Valid/Invalid
```

---

## 4. CustomUserDetailsService

### File: `security/CustomUserDetailsService.java`

```java
package com.gvn.springtutor.security;

import com.gvn.springtutor.entity.User;
import com.gvn.springtutor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Cari user di database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Convert roles ke GrantedAuthority
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        // 3. Return Spring Security UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getIsActive() != null ? user.getIsActive() : true,
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        );
    }
}
```

### Penjelasan:

**UserDetailsService** adalah interface dari Spring Security untuk load user data.

**Flow:**

```
Username → Query Database → Get User Entity → Convert to UserDetails → Return
```

**Kenapa perlu convert ke UserDetails?**

- Spring Security tidak tahu struktur User entity kita
- UserDetails adalah standar interface yang Spring Security pahami
- Berisi: username, password, authorities (roles), dan status flags

**ROLE\_ prefix:**

```java
new SimpleGrantedAuthority("ROLE_" + role.getName())
// "ADMIN" → "ROLE_ADMIN"
// "USER"  → "ROLE_USER"
```

Spring Security mengharuskan prefix "ROLE\_" untuk role-based authorization.

---

## 5. JwtAuthenticationFilter

### File: `security/JwtAuthenticationFilter.java`

```java
package com.gvn.springtutor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Ambil Authorization header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Check apakah ada Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract token (hapus "Bearer " prefix)
        jwt = authHeader.substring(7);

        try {
            // 4. Extract username dari token
            username = jwtUtil.extractUsername(jwt);

            // 5. Jika username ada dan belum authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Load user details dari database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 7. Validate token
                if (jwtUtil.validateToken(jwt, userDetails)) {

                    // 8. Buat authentication token
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    // 9. Set details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 10. Set authentication di SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token invalid, lanjutkan tanpa authentication
        }

        // 11. Lanjutkan ke filter berikutnya
        filterChain.doFilter(request, response);
    }
}
```

### Penjelasan Flow:

```
┌─────────────────────────────────────────────────────────────────┐
│                        HTTP REQUEST                              │
│                 Authorization: Bearer eyJhbG...                  │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ 1. Extract Authorization Header                                  │
│    authHeader = "Bearer eyJhbGciOiJIUzUxMiJ9..."                │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ 2. Check "Bearer " prefix                                        │
│    if (!authHeader.startsWith("Bearer ")) → skip                │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ 3. Extract JWT Token                                             │
│    jwt = authHeader.substring(7)  // Remove "Bearer "           │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ 4. Extract Username from Token                                   │
│    username = jwtUtil.extractUsername(jwt)                      │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ 5. Load UserDetails from Database                                │
│    UserDetails = userDetailsService.loadUserByUsername(username)│
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ 6. Validate Token                                                │
│    if (jwtUtil.validateToken(jwt, userDetails))                 │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ 7. Set Authentication in SecurityContext                         │
│    SecurityContextHolder.getContext().setAuthentication(auth)   │
└─────────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ 8. Continue to Controller                                        │
│    filterChain.doFilter(request, response)                      │
└─────────────────────────────────────────────────────────────────┘
```

### OncePerRequestFilter

Filter ini dijalankan **sekali untuk setiap request**. Setiap HTTP request yang masuk akan melewati filter ini.

---

## 6. SecurityConfig

### File: `security/SecurityConfig.java`

```java
package com.gvn.springtutor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (tidak perlu untuk REST API)
            .csrf(AbstractHttpConfigurer::disable)

            // 2. Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()  // Public endpoints
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()              // Sisanya harus authenticated
            )

            // 3. Stateless session (JWT-based)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 4. Set authentication provider
            .authenticationProvider(authenticationProvider())

            // 5. Add JWT filter SEBELUM UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

### Penjelasan Konfigurasi:

#### 1. Disable CSRF

```java
.csrf(AbstractHttpConfigurer::disable)
```

- **CSRF** (Cross-Site Request Forgery) protection tidak diperlukan untuk REST API
- CSRF protection untuk form-based authentication dengan cookies
- JWT authentication stateless, tidak pakai cookies

#### 2. Authorization Rules

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**").permitAll()  // Public
    .anyRequest().authenticated()              // Protected
)
```

- `/auth/login` dan `/auth/register` bisa diakses tanpa token
- Semua endpoint lain membutuhkan authentication

#### 3. Stateless Session

```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

- Server tidak menyimpan session
- Setiap request harus membawa token sendiri
- Scalable untuk distributed systems

#### 4. Password Encoder

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

- BCrypt adalah standard industry untuk password hashing
- Secure dan resistant terhadap brute force attacks

#### 5. Add JWT Filter

```java
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```

- JWT filter dijalankan SEBELUM default authentication filter
- Ini memastikan token di-validate sebelum request dilanjutkan

---

## 7. DTOs

### AuthRequest.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
}
```

**Untuk:** Login request body

### AuthResponse.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;      // JWT token
    private String type;       // "Bearer"
    private String username;   // Username yang login
    private List<String> roles; // Role user
}
```

**Untuk:** Login/Register response

### RegisterRequest.java

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
```

**Untuk:** Register request body

---

## 8. AuthService

### File: `service/AuthService.java`

```java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Login user dan return JWT token.
     */
    public AuthResponse login(AuthRequest request) {
        // 1. Authenticate dengan Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 3. Generate token
        String token = jwtUtil.generateToken(userDetails);

        // 4. Get user untuk ambil roles
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5. Build response
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Register user baru.
     */
    public AuthResponse register(RegisterRequest request) {
        // 1. Check username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // 2. Get default role (USER)
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        // 3. Create user dengan password encoded
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        // 4. Generate token untuk auto-login setelah register
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .roles(List.of("USER"))
                .build();
    }
}
```

### Login Flow:

```
Request → AuthenticationManager.authenticate() → Success? → Generate Token → Return Response
                         │
                         ▼
              UserDetailsService.loadUserByUsername()
                         │
                         ▼
              PasswordEncoder.matches(rawPassword, encodedPassword)
                         │
                         ▼
                  Match? → Authentication Success
                  No Match? → BadCredentialsException
```

### Register Flow:

```
Request → Check Username Exists → Get Default Role → Encode Password → Save User → Generate Token → Return Response
```

---

## 9. AuthController

### File: `controller/AuthController.java`

```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        try {
            AuthResponse authResponse = authService.login(request);
            return ResponseUtil.ok(authResponse, "Login successful");
        } catch (Exception e) {
            return ResponseUtil.error(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse authResponse = authService.register(request);
            return ResponseUtil.created(authResponse, "User registered successfully");
        } catch (RuntimeException e) {
            return ResponseUtil.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
```

### Endpoints:

| Endpoint         | Method | Request Body                  | Response  |
| ---------------- | ------ | ----------------------------- | --------- |
| `/auth/login`    | POST   | `{username, password}`        | JWT token |
| `/auth/register` | POST   | `{username, email, password}` | JWT token |

---

## 10. Testing

### Register User Baru

```bash
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
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
    "username": "testuser",
    "roles": ["USER"]
  },
  "code": 201
}
```

### Login

```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### Akses Protected Endpoint

```bash
# Tanpa token → 403 Forbidden
curl http://localhost:8081/users

# Dengan token → 200 OK
curl http://localhost:8081/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## Ringkasan

| File                       | Fungsi                                     |
| -------------------------- | ------------------------------------------ |
| `JwtUtil`                  | Generate & validate JWT token              |
| `CustomUserDetailsService` | Load user dari database                    |
| `JwtAuthenticationFilter`  | Filter untuk validate token setiap request |
| `SecurityConfig`           | Konfigurasi Spring Security                |
| `AuthService`              | Business logic login & register            |
| `AuthController`           | REST endpoints `/auth/*`                   |

### Security Flow Complete:

```
                                    ┌─────────────────┐
                                    │   /auth/login   │
                                    │   /auth/register│
                                    │    (Public)     │
                                    └────────┬────────┘
                                             │
┌──────────────────────────────────────────────────────────────────────┐
│                         JwtAuthenticationFilter                       │
│  ┌─────────────┐    ┌──────────────┐    ┌─────────────────────────┐  │
│  │ Get Header  │───▶│ Extract JWT  │───▶│ Validate & Set Auth     │  │
│  └─────────────┘    └──────────────┘    └─────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │   /users        │
                                    │   /roles        │
                                    │   /products     │
                                    │   (Protected)   │
                                    └─────────────────┘
```
