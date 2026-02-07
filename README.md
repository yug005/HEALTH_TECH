# HealthTech Demo Application

A full-stack health-tech demo application demonstrating modern Java/Spring Boot practices with a simple vanilla JavaScript frontend.

---

## 🏗️ Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Java 17** | Modern Java with streams, lambdas, Optional |
| **Spring Boot 3.2** | Application framework |
| **Spring Security** | JWT-based authentication |
| **Spring Data JPA** | ORM with Hibernate |
| **PostgreSQL** | Relational database |
| **Maven** | Build tool |
| **JJWT** | JWT token handling |

### Frontend
| Technology | Purpose |
|------------|---------|
| **HTML5** | Page structure |
| **CSS3** | Styling |
| **Vanilla JavaScript** | API calls & DOM manipulation |
| **Fetch API** | REST communication |

---

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐                  │
│  │index.html│  │style.css │  │  script.js   │                  │
│  └──────────┘  └──────────┘  └──────────────┘                  │
│                        │ fetch() API calls                      │
└────────────────────────┼────────────────────────────────────────┘
                         │ HTTP + JWT Token
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT BACKEND                          │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    CONTROLLER LAYER                        │ │
│  │  AuthController │ DoctorController │ AppointmentController │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              │                                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                     SERVICE LAYER                          │ │
│  │   AuthService   │  DoctorService   │  AppointmentService   │ │
│  │                                    │  (Concurrency Logic)  │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              │                                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                    REPOSITORY LAYER                        │ │
│  │  UserRepository │ DoctorRepository │ AppointmentRepository │ │
│  └────────────────────────────────────────────────────────────┘ │
│                              │                                   │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                     ENTITY LAYER                           │ │
│  │       User      │     Doctor       │     Appointment       │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                         │
                         ▼
              ┌──────────────────────┐
              │     PostgreSQL       │
              │      Database        │
              └──────────────────────┘
```

---

## 📡 API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Doctors (GET = Public, Others = Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctors` | List all doctors |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| GET | `/api/doctors/available` | List available doctors |
| GET | `/api/doctors/specialty/{specialty}` | Filter by specialty |
| GET | `/api/doctors/specialties` | List all specialties |
| POST | `/api/doctors` | Add new doctor (auth required) |
| PUT | `/api/doctors/{id}` | Update doctor (auth required) |
| PATCH | `/api/doctors/{id}/availability` | Toggle availability (auth required) |

### Appointments (All Authenticated)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments` | Book appointment |
| GET | `/api/appointments/my` | Get my appointments |
| GET | `/api/appointments/upcoming` | Get upcoming appointments |
| GET | `/api/appointments/{id}` | Get appointment by ID |
| DELETE | `/api/appointments/{id}` | Cancel appointment |

---

## 🔐 Security Flow

```
1. User Registration/Login
   └─→ POST /api/auth/register or /api/auth/login
       └─→ Backend validates credentials
           └─→ JWT token generated and returned

2. Authenticated Request
   └─→ Frontend stores JWT in localStorage
       └─→ Each request includes: Authorization: Bearer <token>
           └─→ JwtAuthenticationFilter validates token
               └─→ SecurityContext populated with user info
                   └─→ Controller can access user via Authentication object
```

---

## ⚡ Concurrency Handling (Double-Booking Prevention)

The appointment booking system implements **4 levels of concurrency control**:

### Level 1: Application-Level Lock (ReentrantLock)
```java
private final ReentrantLock bookingLock = new ReentrantLock(true);
// Fair mode ensures FIFO processing
bookingLock.lock();
try {
    // booking logic
} finally {
    bookingLock.unlock();
}
```

### Level 2: Database Pessimistic Lock
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentTime = :time")
Optional<Appointment> findByDoctorAndTimeWithLock(...);
```

### Level 3: Optimistic Locking (@Version)
```java
@Entity
public class Appointment {
    @Version
    private Long version; // Auto-incremented on each update
}
```

### Level 4: Database Unique Constraint
```java
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"doctor_id", "appointment_time"})
})
```

### Why Multiple Levels?
- **Defense in depth** - if one fails, others catch it
- **Level 1** - Fast for single JVM deployments
- **Level 2** - Works across distributed instances
- **Level 3** - Catches edge cases during save
- **Level 4** - Ultimate database-level safety

---

## 🎯 SOLID Principles Applied

### Single Responsibility (SRP)
- Each service handles one domain (Auth, Doctor, Appointment)
- Controllers only handle HTTP concerns
- Repositories only handle data access

### Open/Closed (OCP)
- New features can be added via new services
- Exception handling extensible via GlobalExceptionHandler

### Liskov Substitution (LSP)
- Repository interfaces can have different implementations

### Interface Segregation (ISP)
- Separate DTOs for requests and responses
- Minimal interfaces for each purpose

### Dependency Inversion (DIP)
- Controllers depend on service interfaces, not implementations
- Spring DI manages dependencies

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 15+

### Database Setup
```sql
CREATE DATABASE healthtech_db;
-- Tables are auto-created by Hibernate
```

### Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/healthtech_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run Backend
```bash
cd healthtech-demo
mvn spring-boot:run
```
Backend runs on: http://localhost:8080

### Run Frontend
Open `frontend/index.html` in a browser, or use a local server:
```bash
cd frontend
# Using Python
python -m http.server 5500
# Or use VS Code Live Server
```
Frontend runs on: http://localhost:5500

---

## 📁 Project Structure

```
healthtech-demo/
├── pom.xml                          # Maven configuration
├── src/main/
│   ├── java/com/healthtech/
│   │   ├── HealthTechApplication.java   # Main entry point
│   │   ├── config/
│   │   │   └── DataInitializer.java     # Sample data loader
│   │   ├── controller/
│   │   │   ├── AuthController.java      # Login/Register endpoints
│   │   │   ├── DoctorController.java    # Doctor CRUD endpoints
│   │   │   ├── AppointmentController.java # Booking endpoints
│   │   │   └── HealthController.java    # Health check
│   │   ├── dto/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── DoctorRequest.java
│   │   │   ├── DoctorResponse.java
│   │   │   ├── AppointmentRequest.java
│   │   │   ├── AppointmentResponse.java
│   │   │   └── ApiResponse.java         # Generic response wrapper
│   │   ├── entity/
│   │   │   ├── User.java                # User entity
│   │   │   ├── Doctor.java              # Doctor entity
│   │   │   └── Appointment.java         # Appointment entity (with @Version)
│   │   ├── exception/
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── DuplicateResourceException.java
│   │   │   ├── InvalidCredentialsException.java
│   │   │   ├── ConflictException.java
│   │   │   └── GlobalExceptionHandler.java  # @ControllerAdvice
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── DoctorRepository.java
│   │   │   └── AppointmentRepository.java   # Has pessimistic lock query
│   │   ├── security/
│   │   │   ├── JwtService.java              # JWT generation/validation
│   │   │   ├── JwtAuthenticationFilter.java # Request filter
│   │   │   └── SecurityConfig.java          # Security configuration
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── DoctorService.java
│   │       └── AppointmentService.java      # Concurrency handling
│   └── resources/
│       └── application.properties
├── frontend/
│   ├── index.html                    # Single-page app
│   ├── style.css                     # Styling
│   └── script.js                     # API calls & logic
└── README.md
```

---

## 📝 Code Comments

All code files contain clear comments explaining:
- **Purpose** of each class/function
- **Flow** of operations
- **Design decisions** and why they were made
- **Concurrency handling** mechanisms
- **Frontend-Backend communication**

---

## 🧪 Testing the Application

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@test.com","password":"password123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@test.com","password":"password123"}'
```

### 3. Get Doctors (Public)
```bash
curl http://localhost:8080/api/doctors
```

### 4. Book Appointment (Authenticated)
```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"doctorId":1,"appointmentTime":"2024-12-20T10:00:00","notes":"Checkup"}'
```

---

## 🎓 Interview Ready Features

1. **Layered Architecture** - Clean separation of concerns
2. **Spring Security + JWT** - Stateless authentication
3. **Spring Data JPA** - ORM with query methods
4. **Global Exception Handling** - @ControllerAdvice pattern
5. **Concurrency Control** - Multi-level locking strategy
6. **SOLID Principles** - Clean, maintainable code
7. **Java 17 Features** - Streams, lambdas, Optional
8. **DTOs** - Request/Response separation
9. **Input Validation** - Bean Validation annotations
10. **CORS Configuration** - Frontend-backend communication

---

## 📄 License

This is a demo project for educational purposes.
