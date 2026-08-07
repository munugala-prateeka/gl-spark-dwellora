# Dwellora

**Unified Apartment Community & Resident Services Platform**
GlobalLogic Java Track TE 2.0 — IAP Sprint Capstone

Dwellora is a microservice-based platform that digitizes day-to-day operations within residential apartment complexes. Each apartment community is onboarded as an independent tenant with its own manager, residents, amenities, bookings, complaints, notices, and events.

---

## Architecture

All client traffic is routed through a single **API Gateway**, and every service registers with and discovers others dynamically via a **Eureka** service registry — no hardcoded service locations.

| Service | Responsibility | Port |
|---|---|---|
| **EurekaServer** | Service registry & discovery | 8761 |
| **ApiGateway** | Single entry point, routes to all downstream services | 8769 |
| **OnboardingService** | Onboarding requests (PENDING/APPROVED/REJECTED), triggers provisioning | 8081 |
| **ApartmentService** | Apartment/society tenant records | 8082 |
| **UserService** | Managers & residents, auth (JWT), account activation | 8083 |
| **AmenityService** | Amenity catalog, booking policy config | 8084 |
| **BookingService** | Slot booking, cancellation, availability | 8085 |
| **MaintenanceService** | Resident complaints, manager resolution | 8087 |
| **NoticeService** | Manager-published notices (urgent/routine) | 8088 |
| **EventService** | Community events & RSVP | 8089 |
| **NotificationService** | In-app notifications + transactional email | 8086 |

**Onboarding provisioning flow (Kafka-driven):**
```
Platform Admin approves request
  → OnboardingService (publishes CommunityApprovedEvent)
  → ApartmentService (creates Apartment, publishes ApartmentCreatedEvent)
  → UserService (creates Manager as PENDING_ACTIVATION, publishes ManagerCreatedEvent)
  → NotificationService (sends activation email)
```
Adding a resident and resident activation follow the same producer/consumer pattern (`ResidentCreatedEvent`).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.5.x, Spring Cloud 2025.0.0 |
| Frontend | React + TypeScript + Vite + MUI |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Messaging | Apache Kafka |
| Database | PostgreSQL (one schema per service) |
| Auth | Spring Security + JWT (UserService) |
| Email | Spring Boot Mail (JavaMailSender) |
| Testing | JUnit 5 + Mockito |
| Version Control | Git — feature-branch-per-story workflow |

---

## Prerequisites

- Java 17
- Maven (or use the included `mvnw` / `mvnw.cmd` wrapper per service)
- Node.js 18+ and npm
- PostgreSQL running locally on port `5432`
- Apache Kafka + Zookeeper running locally on port `9092`

---

## Repository Structure

```
gl-spark-dwellora/
├── EurekaServer/
├── ApiGateway/
├── OnboardingService/
├── ApartmentService/
├── UserService/
├── AmenityService/
├── BookingService/
├── MaintenanceService/
├── NoticeService/
├── EventService/
├── NotificationService/
├── dwellora-ui/          # React + TypeScript frontend
├── pom.xml               # parent POM (Maven multi-module)
└── README.md
```

---

## Setup & Run

### 1. Database
Create a PostgreSQL database per service (see each service's `application.properties` for the expected DB name), or adjust connection settings via environment variables:
```bash
DB_USER=your_user
DB_PASS=your_password
```

### 2. Kafka
Start Zookeeper and Kafka locally on the default ports before starting any backend service — `OnboardingService`, `ApartmentService`, `UserService`, and `NotificationService` all depend on it for the provisioning workflow.

### 3. Backend — start in this order
```bash
# 1. Service registry first
cd EurekaServer && ./mvnw spring-boot:run

# 2. Gateway
cd ApiGateway && ./mvnw spring-boot:run

# 3. Remaining services (any order, once Eureka is up)
cd OnboardingService && ./mvnw spring-boot:run
cd ApartmentService && ./mvnw spring-boot:run
cd UserService && ./mvnw spring-boot:run
cd AmenityService && ./mvnw spring-boot:run
cd BookingService && ./mvnw spring-boot:run
cd MaintenanceService && ./mvnw spring-boot:run
cd NoticeService && ./mvnw spring-boot:run
cd EventService && ./mvnw spring-boot:run
cd NotificationService && ./mvnw spring-boot:run
```
Confirm all services are registered at **http://localhost:8761**.

To build and run all tests for a single service:
```bash
cd <ServiceName>
./mvnw clean test        # run unit tests
./mvnw clean package      # build the jar
```

### 4. Frontend
```bash
cd dwellora-ui
npm install
npm run dev
```
Runs at **http://localhost:5173**, talking to the backend exclusively through the ApiGateway at `http://localhost:8769`.

---

## Roles & Access

| Role | How the account is created |
|---|---|
| **PLATFORM_ADMIN** | Seeded directly in the database — not self-service |
| **MANAGER** | Provisioned automatically when a platform admin approves an onboarding request; activates via emailed token link |
| **RESIDENT** | Added by their apartment manager; activates via emailed token link |

No role ever receives a plaintext password — every new account is created with status `PENDING_ACTIVATION` and no password, and the owner sets their own password via a time-limited, single-use activation token.

---

## Testing

Each service carries a minimum of 4 JUnit 5 unit tests using Mockito, covering the core service-layer logic (success paths, not-found/exception cases, and business-rule validation). Run per service:
```bash
cd <ServiceName>
./mvnw clean test
```


---

## Branching & Commit Convention

- `main` — production-ready, stable code only
- `develop` — integration branch for all features
- `feature/US-XXX` — one branch per user story, PR'd into `develop`
- `test/service-unit-tests` — JUnit/Mockito test additions
- Commit messages follow Conventional Commits: `feat(US-XXX): ...`, `fix(...)`, `test(US-XXX): ...`, `chore: ...`, `refactor: ...`

---

**Author:** Munugala Prateeka

**Program:** GlobalLogic Java Track TE 2.0 — IAP Sprint Capstone
