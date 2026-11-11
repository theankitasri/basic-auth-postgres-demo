# Spring Security Demo

A Spring Boot 3.4 project demonstrating **HTTP Basic authentication** with users persisted in **PostgreSQL** and passwords encoded with **BCrypt**.

## Prerequisites

- Java 17+ (JDK)
- Maven 3.9+
- PostgreSQL 14+ (running, with a database named `securitydb`)

Create the database:

```sql
CREATE DATABASE securitydb;
```

## Setup

1. **Configure the database** — copy the example properties file and fill in your password:

   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

   Then edit `application.properties` with your Postgres credentials:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/securitydb
   spring.datasource.username=postgres
   spring.datasource.password=<your-password>
   ```

   `application.properties` is gitignored, so your real password never leaves your machine.

2. **Run the app:**

   ```bash
   mvn spring-boot:run
   ```

   On first startup, Hibernate creates the `users` table (`ddl-auto=update`) and the app seeds two demo users.

## Seed users

| Username | Password   |
|----------|------------|
| `alice`  | `passw0rd` |
| `bob`    | `secret456`|

## API

All endpoints except `/api/register` require HTTP Basic auth.

### Register a user (no auth required)

```bash
curl -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{"username":"charlie","displayName":"Charlie","password":"strongpass123"}'
```

**Response `201 Created`:**
```json
{"username":"charlie","displayName":"Charlie"}
```

- `400 Bad Request` — blank username or password under 8 chars
- `409 Conflict` — username already exists

### List all users

```bash
curl -u alice:passw0rd http://localhost:8080/api/users
```

**Response `200 OK`:**
```json
[
  {"username":"alice","displayName":"Alice"},
  {"username":"bob","displayName":"Bob"}
]
```

### Get a single user

```bash
curl -u alice:passw0rd http://localhost:8080/api/users/charlie
```

**Response `200 OK`:**
```json
{"username":"charlie","displayName":"Charlie"}
```

### Get the authenticated user's profile

```bash
curl -u bob:secret456 http://localhost:8080/api/me
```

**Response `200 OK`:**
```json
{"username":"bob","message":"Authenticated via HTTP Basic"}
```

### Auth failures

Wrong or missing credentials return **`401 Unauthorized`** with a `WWW-Authenticate: Basic` header.

## Project structure

```
src/main/java/com/example/springsecuritydemo/
├── SpringSecurityDemoApplication.java   # main class
├── config/
│   ├── SecurityConfig.java              # Basic auth + BCrypt + UserDetailsService
│   └── DataInitializer.java             # seeds alice & bob on first run
├── controller/
│   ├── UserController.java              # REST endpoints
│   └── GlobalExceptionHandler.java      # 400/409 error mapping
├── dto/
│   ├── UserRequest.java                 # register request body
│   └── UserResponse.java               # response body (no password leak)
├── model/
│   └── User.java                        # JPA entity → users table
├── repository/
│   └── UserRepository.java              # Spring Data JPA repo
└── service/
    └── UserService.java                 # register / find / list logic
```

## Security notes

- Passwords are stored as **BCrypt** hashes — never in plaintext.
- `UserResponse` omits the password field so it's never serialized into an API response.
- In production, serve over **HTTPS** — HTTP Basic transmits credentials in the header, which is only safe over TLS.
- Consider adding roles (e.g. `ADMIN`) and restricting `/api/users` to admin-only in a real system.
