# Dwellora

**Unified Apartment Community & Resident Services Platform**

GlobalLogic Java Track TE 2.0 — IAP Sprint Capstone

Dwellora is a microservice-based platform that digitizes day-to-day operations
for residential apartment communities. It handles amenity bookings, maintenance
complaints, notices, community events, and resident onboarding, with each
apartment complex kept fully isolated from every other one on the platform.

---

## Architecture

```
                     Frontend (React + TypeScript, Vite)
                                    │
                                    │  Authorization: Bearer <JWT>
                                    ▼
                         ┌─────────────────────┐
                         │   API Gateway :8769  │  ← validates JWT signature
                         │  (Spring Cloud GW)   │    + expiry ONCE, forwards
                         └──────────┬───────────┘    X-User-Id / X-User-Role /
                                    │                 X-Apartment-Id headers
              ┌─────────────────────┼─────────────────────┐
              ▼                     ▼                     ▼
     ┌────────────────┐   ┌─────────────────┐   ┌──────────────────┐
     │  UserService    │   │ OnboardingService│   │ ApartmentService │
     │     :8083       │   │      :8081       │   │      :8082       │
     └────────┬────────┘   └────────┬─────────┘   └──────────────────┘
              │                     │  Kafka: community-approved →
              │                     │         apartment-created →
              │                     │         manager-created
              ▼                     ▼
     ┌────────────────┐   ┌──────────────────┐   ┌──────────────────┐
     │ AmenityService  │   │  BookingService  │   │ MaintenanceService│
     │     :8084       │   │      :8085       │   │      :8088        │
     └────────────────┘   └──────────────────┘   └──────────────────┘

     ┌────────────────┐   ┌──────────────────┐   ┌──────────────────┐
     │  NoticeService  │   │   EventService   │   │NotificationService│
     │     :8089       │   │      :8086       │   │      :8087        │
     └────────────────┘   └──────────────────┘   └──────────────────┘
                                                   ↑ consumes every
                                                     domain event via Kafka
                                                     for in-app + email
                                                     notifications

     Eureka Server :8761 — service registry, every service above
     registers with it and discovers each other by name for both
     REST (Feign) and Gateway routing.
```

Every service is independently deployable, owns its own PostgreSQL database,
and communicates with its siblings two ways:

- **Synchronous** — direct Feign calls between services (e.g. `BookingService`
  calling `AmenityService` to check slot capacity), resolved via Eureka. 

- **Asynchronous** — Kafka events for anything that fans out or doesn't need
  an immediate response: onboarding provisioning, and every domain event that
  needs to notify a resident (booking confirmed, complaint updated, notice
  published, event created, RSVP confirmed/cancelled).

---

## Services

| Service | Port | Owns | Responsibility |
|---|---|---|---|
| EurekaServer | 8761 | — | Service registry / discovery |
| ApiGateway | 8769 | — | Single entry point, JWT validation, routing |
| UserService | 8083 | `dwellora_user` | Auth, login, account activation, resident CRUD |
| OnboardingService | 8081 | `dwellora_onboarding` | Society onboarding requests, admin approval |
| ApartmentService | 8082 | `dwellora_apartment` | Apartment/tenant records |
| AmenityService | 8084 | `dwellora_amenity` | Amenities + booking policy config |
| BookingService | 8085 | `dwellora_booking` | Amenity slot booking, availability, cancellation |
| MaintenanceService | 8088 | `dwellora_maintenance` | Resident complaints, manager resolution |
| NoticeService | 8089 | `dwellora_notice` | Community notices |
| EventService | 8086 | `dwellora_event` | Community events + RSVPs |
| NotificationService | 8087 | `dwellora_notification` | In-app notifications + activation/transactional email |

Each service is a standalone Maven module built against a shared parent POM
(`gl-spark-dwellora`)

---

## Security model

**Validated once, at the Gateway — every downstream service trusts what it forwards.**

1. `UserService` issues a JWT on login (`POST /users/login`), signed HS256,
   containing `userId`, `role`, and `apartmentId` (omitted for `PLATFORM_ADMIN`,
   who isn't tied to one apartment).
2. `ApiGateway`'s `JwtGlobalFilter` validates the signature + expiry on every
   request, **strips any client-supplied `X-User-Id` / `X-User-Role` /
   `X-Apartment-Id` headers first** (so a caller can't spoof identity by just
   setting those headers themselves), then writes its own trusted versions
   derived from the verified token.
3. Every downstream service  runs a small `HeaderAuthFilter` that reads
   those three headers and populates Spring Security's context. 
4. `@PreAuthorize("hasRole('...')")` on each controller method enforces the
   role required per endpoint (see table below). Endpoints without an
   annotation just require `.authenticated()` — any logged-in role.

**Public (no token required):**
- `POST /users/login`
- `POST /users/activate`
- `POST /onboarding/request`

### Role requirements by endpoint (non-exhaustive, see each controller)

| Service | Endpoint | Role |
|---|---|---|
| UserService | `GET /users` | `PLATFORM_ADMIN` |
| UserService | `POST/GET/PUT/DELETE /users/residents`, `/users/{id}` | `MANAGER` |
| OnboardingService | `GET/PUT` (pending, approve, reject) | `PLATFORM_ADMIN` |
| AmenityService | `POST/PUT/DELETE /amenities` | `MANAGER` |
| BookingService | `POST /bookings`, `PUT /bookings/cancel/{id}`, `GET /bookings/my`, `GET /bookings/availability/{id}` | `RESIDENT` |
| BookingService | `GET /bookings`, `GET /bookings/apartment/{id}(/today/count)` | `MANAGER` |
| MaintenanceService | `POST /complaints`, `GET /complaints/my` | `RESIDENT` |
| MaintenanceService | `GET /complaints`, `PUT /complaints/{id}` | `MANAGER` |
| NoticeService | `POST/DELETE /notices` | `MANAGER` |
| NoticeService | `GET /notices` | `RESIDENT` or `MANAGER` |
| EventService | `POST /events` | `MANAGER` |
| EventService | `POST/DELETE /events/{id}/rsvp`, `GET /events/my-rsvps` | `RESIDENT` |
| EventService | `GET /events` | `RESIDENT` or `MANAGER` |
| NotificationService | `GET /notifications/user/{id}` | self-only (ownership check, not role-based) |

Booking cancellation and complaint/notification access additionally enforce
**ownership** — a resident can only cancel their own booking or view their
own notifications, checked against the trusted `X-User-Id` header, not
client-supplied input.

---

## Resilience

`BookingService` wraps its outbound Feign calls (`UserClient`, `AmenityClient`)
with a Resilience4j circuit breaker + retry, configured via
`resilience4j.*.configs.default` (applies automatically to every Feign client
in the service, no per-client naming to get wrong):

- Opens after ≥50% failure rate across a 10-call sliding window (min. 5 calls)
- Stays open 10s, then probes with 3 calls before fully closing again
- Retries failed calls up to 3× with exponential backoff (500ms, 1000ms, ...)

When `AmenityService`/`UserService` is unreachable, booking requests fail fast
with a clear error instead of hanging on a TCP timeout.

---

## Prerequisites

- **Java 17**
- **Maven 3.9+**
- **PostgreSQL 14+** running locally (or update `DB_URL`/credentials to point
  elsewhere)
- **Apache Kafka** running locally on `localhost:9092` (with Zookeeper, or
  KRaft mode — either works, nothing here is version-pinned to a specific
  Kafka setup)
- A **Gmail account with an App Password** (not your regular password) for
  `NotificationService` to send activation/transactional email

### Databases

Each service owns its schema and creates/updates its own tables on startup
(`spring.jpa.hibernate.ddl-auto=update`),  you only need to create the empty
databases up front:

```sql
CREATE DATABASE dwellora_user;
CREATE DATABASE dwellora_onboarding;
CREATE DATABASE dwellora_apartment;
CREATE DATABASE dwellora_amenity;
CREATE DATABASE dwellora_booking;
CREATE DATABASE dwellora_maintenance;
CREATE DATABASE dwellora_notice;
CREATE DATABASE dwellora_event;
CREATE DATABASE dwellora_notification;
```

### Environment variables

All of these have sane local defaults baked into each `application.properties`
except the two marked **required** — set those or `NotificationService` won't
start at all.

| Variable | Default | Notes |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/<db_name>` | per-service, see table above |
| `DB_USERNAME` | `postgres` | |
| `DB_PASSWORD` | `your_local_password` | change this |
| `EUREKA_SERVER_URL` | `http://localhost:8761/eureka/` | |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | |
| `JWT_SECRET` | `dwellora_super_secret_key_that_is_at_least_256_bits_long_for_hs256` | must be **identical** across UserService and ApiGateway — they're the only two services that touch it |
| `MAIL_USERNAME` | *(none — required)* | Gmail address NotificationService sends from |
| `MAIL_PASSWORD` | *(none — required)* | Gmail **App Password**, not your login password |

---

## Running locally

Start in this order: each service registers with Eureka on boot and needs it
running first; services calling each other via Feign need their target
registered before the call, not before their own startup.

```bash
# 1. Registry — wait for it to fully come up before continuing
cd EurekaServer && mvn spring-boot:run

# 2. Gateway
cd ApiGateway && mvn spring-boot:run

# 3. Everything else — order doesn't matter much between these,
#    but give Eureka ~30s from step 1 before starting them
cd UserService && mvn spring-boot:run
cd OnboardingService && mvn spring-boot:run
cd ApartmentService && mvn spring-boot:run
cd AmenityService && mvn spring-boot:run
cd BookingService && mvn spring-boot:run
cd MaintenanceService && mvn spring-boot:run
cd NoticeService && mvn spring-boot:run
cd EventService && mvn spring-boot:run
cd NotificationService && mvn spring-boot:run
```

Check the Eureka dashboard at `http://localhost:8761` — all 10 services (9
above + the Gateway) should show as `UP` before you start testing.

There is currently no seeded `PLATFORM_ADMIN` account and no API endpoint to
create one (by design admins aren't self-service). Insert one directly:

```sql
INSERT INTO users (email, full_name, password, role, account_status)
VALUES (
    'admin@dwellora.com',
    'Dwellora Platform',
    '<bcrypt-hash-of-your-chosen-password>',
    'PLATFORM_ADMIN',
    'ACTIVE'
);
```

Everything after that: onboarding a society, approving it, activating the
manager, adding residents, flows entirely through the API/UI from there.

---

## Testing

Each service has its own JUnit 5 + Mockito unit test suite under
`src/test/java`, run independently per module:

```bash
cd UserService && mvn test
cd BookingService && mvn test
# ...repeat per service
```

Coverage focuses on service-layer business logic (booking policy conflicts,
onboarding approval/rejection, complaint lifecycle, RSVP capacity, etc.) with
mocked repositories and Feign clients.

---

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.x, Spring Cloud Gateway, Spring Cloud OpenFeign |
| Service discovery | Netflix Eureka |
| Messaging | Apache Kafka (Spring Kafka) |
| Resilience | Resilience4j (circuit breaker + retry) |
| Auth | JWT (`io.jsonwebtoken` / jjwt 0.12.x), Spring Security, BCrypt |
| Persistence | PostgreSQL, Spring Data JPA / Hibernate |
| Email | Spring Mail (Gmail SMTP) |
| Testing | JUnit 5, Mockito |
| Frontend | React + TypeScript, Vite, Material UI |


## Branching & Commit Convention

- `main` — production-ready, stable code only
- `develop` — integration branch for all features
- `feature/US-XXX` — one branch per user story, PR'd into `develop`
- `test/service-unit-tests` — JUnit/Mockito test additions
- Commit messages follow Conventional Commits: `feat(US-XXX): ...`, `fix(...)`, `test(US-XXX): ...`, `chore: ...`, `refactor: ...`

---

**Author:** Munugala Prateeka

**Program:** GlobalLogic Java Track TE 2.0 — IAP Sprint Capstone

