# EasyLodge — Backend API

A production-grade hotel booking REST API built with **Java 21** and **Spring Boot 3.5**, featuring JWT-based authentication, Stripe payment integration, concurrent booking with transactional integrity guarantees, and a dynamic pricing engine.

**Frontend Repository:** [easylodge-frontend](https://github.com/RohanKumar110/easylodge-frontend)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.3 |
| Security | Spring Security + JJWT 0.12.6 |
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Payments | Stripe Java SDK 29.3.0 |
| Validation | Spring Boot Validation (Bean Validation) |
| Build | Maven (Maven Wrapper) |
| Utilities | Lombok, Apache Commons Lang3 |

---

## Architecture Overview

The application follows a standard layered architecture:

```
Controller (REST) → Service (Business Logic) → Repository (JPA) → PostgreSQL
```

Cross-cutting concerns — authentication, authorization, input validation, and error handling — are applied globally through Spring Security filters and Bean Validation constraints, keeping controller and service layers clean.

---

## Key Features & Design Decisions

### JWT Authentication (Stateless)
Authentication is implemented via Spring Security with JJWT 0.12.6. On login, the server issues a signed JWT containing the user's identity and role claims. Subsequent requests are validated against the token signature — no server-side session state is maintained, making the API horizontally scalable.

Role-based access control (RBAC) enforces two roles:
- `GUEST` — search, view availability, create and manage own bookings
- `ADMIN` — manage hotel inventory, view all bookings, update room status

### Concurrent Booking with Transactional Integrity
The booking flow uses pessimistic locking at the database level to prevent race conditions under simultaneous reservation attempts. When a booking request arrives, the system:
1. Acquires a row-level lock on the target room record
2. Validates real-time availability within the same transaction
3. Commits or rolls back atomically

This guarantees that double-booking cannot occur even under concurrent high-load requests.

### Booking State Machine
Each booking follows a strict lifecycle enforced at the service layer:

```
PENDING → CONFIRMED → PAID → COMPLETED
                   ↘ CANCELLED
              FAILED (on payment error)
```

Transitions are validated before execution. Invalid state changes (e.g., paying a cancelled booking) are rejected with an appropriate error response.

### Stripe Payment Integration
Payment processing is handled via the Stripe Java SDK. The API creates a Stripe PaymentIntent on booking confirmation and handles webhook events for payment success and failure. On payment failure, the booking is automatically transitioned to `FAILED` state and the room availability is restored.

### Dynamic Pricing Engine (Strategy Pattern)
Room pricing is decoupled from room data using the Strategy pattern. A `PricingStrategy` interface with multiple implementations allows different pricing rules to be applied at runtime based on occupancy levels, seasonal demand, and room type — without modifying core booking logic.

### Input Validation
All incoming request payloads are validated using Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`, `@Future`, custom constraints). Validation errors return structured `400 Bad Request` responses with per-field error messages.

---

## API Endpoints

### Authentication
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Authenticate and receive JWT |

### Hotels & Rooms
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/hotels` | Public | List all hotels |
| `GET` | `/api/hotels/{id}` | Public | Get hotel details |
| `GET` | `/api/hotels/{id}/rooms` | Public | List available rooms |
| `GET` | `/api/rooms/{id}/availability` | Public | Check room availability for dates |
| `POST` | `/api/hotels` | Admin | Create hotel |
| `PUT` | `/api/rooms/{id}` | Admin | Update room details |

### Bookings
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/bookings` | Guest | Create a new booking |
| `GET` | `/api/bookings/{id}` | Guest/Admin | Get booking details |
| `GET` | `/api/bookings/my` | Guest | Get current user's bookings |
| `PUT` | `/api/bookings/{id}/cancel` | Guest | Cancel a booking |
| `GET` | `/api/admin/bookings` | Admin | View all bookings |

### Payments
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/payments/intent` | Guest | Create Stripe PaymentIntent |
| `POST` | `/api/payments/webhook` | System | Handle Stripe webhook events |

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 15+
- Stripe account (for payment features)

### 1. Clone the repository
```bash
git clone https://github.com/RohanKumar110/easylodge.git
cd easylodge
```

### 2. Configure environment variables
Create an `application-local.properties` file under `src/main/resources/` or export the following environment variables:

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/easylodge
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_256bit_secret_key
JWT_EXPIRATION_MS=86400000

# Stripe
STRIPE_SECRET_KEY=sk_test_your_stripe_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret
```

### 3. Create the database
```sql
CREATE DATABASE easylodge;
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 5. Run tests
```bash
./mvnw test
```

---

## Project Structure

```
src/main/java/dev/rohankumar/easylodge_app/
├── auth/               # JWT filter, token utility, auth controller
├── booking/            # Booking entity, service, state machine, repository
├── hotel/              # Hotel and room entities, controllers, repositories
├── payment/            # Stripe integration, payment service, webhook handler
├── pricing/            # PricingStrategy interface and implementations
├── user/               # User entity, RBAC roles, UserDetailsService
├── config/             # Spring Security config, CORS config, Bean config
└── exception/          # Global exception handler, custom exceptions
```

---

## Security Configuration

- JWT tokens expire after 24 hours (configurable)
- Passwords are hashed with BCrypt
- CORS is configured to allow requests from the frontend origin
- Public endpoints (`/api/auth/**`, `GET /api/hotels/**`) are accessible without authentication
- All other endpoints require a valid JWT in the `Authorization: Bearer <token>` header
- Admin-only endpoints are protected with `@PreAuthorize("hasRole('ADMIN')")`

---

## Notes

- The application uses `spring.jpa.hibernate.ddl-auto=update` for schema management in development. For production, use Flyway or Liquibase migrations.
- Stripe webhook signature verification is enforced in production mode. For local testing, use the Stripe CLI to forward webhook events.
