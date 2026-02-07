# 🏥 HealthTech Demo - Appointment Booking System

A full-stack healthcare appointment booking application built with **Spring Boot** and **vanilla JavaScript**. Features secure JWT authentication, real-time doctor availability, and **multi-level concurrency control** to prevent double bookings.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

---

## ✨ Features

- 🔐 **JWT Authentication** - Secure login/register with token-based auth
- 👨‍⚕️ **Doctor Management** - Browse doctors, filter by specialty
- 📅 **Appointment Booking** - Book appointments with available doctors
- ⚡ **Concurrency Control** - Multi-level locking prevents double bookings
- 🎨 **Responsive UI** - Clean, modern frontend design

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| Java 17 | Core language with modern features |
| Spring Boot 3.2 | Application framework |
| Spring Security | JWT-based authentication |
| Spring Data JPA | ORM with Hibernate |
| PostgreSQL | Relational database |
| Maven | Build & dependency management |

### Frontend
| Technology | Purpose |
|------------|---------|
| HTML5 | Structure |
| CSS3 | Styling with modern design |
| JavaScript | Vanilla JS with Fetch API |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (HTML/CSS/JS)                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    REST API (Spring Boot)                    │
├─────────────┬─────────────┬─────────────┬───────────────────┤
│ Controllers │  Services   │ Repositories│    Security       │
│  (REST)     │  (Business) │   (Data)    │    (JWT)          │
└─────────────┴─────────────┴─────────────┴───────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                       │
│  ┌─────────┐  ┌─────────┐  ┌──────────────┐                 │
│  │  Users  │  │ Doctors │  │ Appointments │                 │
│  └─────────┘  └─────────┘  └──────────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

---

## ⚡ Concurrency Handling

The appointment booking system implements **4 levels of concurrency control** to ensure no double bookings occur:

| Level | Mechanism | Purpose |
|-------|-----------|---------|
| 1 | `ReentrantLock` | Application-level thread synchronization |
| 2 | `@Lock(PESSIMISTIC_WRITE)` | Database row locking |
| 3 | `@Version` | Optimistic locking with version check |
| 4 | Unique Constraint | Database-level final safeguard |

```java
// Example: Multi-level concurrency in AppointmentService
private final ReentrantLock bookingLock = new ReentrantLock(true);

@Transactional(isolation = Isolation.SERIALIZABLE)
public AppointmentResponse bookAppointment(request) {
    bookingLock.lock();
    try {
        // Pessimistic lock check
        Optional<Appointment> existing = repository.findByDoctorAndTimeWithLock(...);
        if (existing.isPresent()) {
            throw new ConflictException("Slot already booked");
        }
        // Book appointment (protected by all 4 levels)
        return save(appointment);
    } finally {
        bookingLock.unlock();
    }
}
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 15+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yug005/HEALTH_TECH.git
   cd HEALTH_TECH
   ```

2. **Create PostgreSQL database**
   ```sql
   CREATE DATABASE healthtech_db;
   ```

3. **Configure database** (src/main/resources/application.properties)
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/healthtech_db
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Frontend: http://localhost:8080
   - API: http://localhost:8080/api

---

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login & get JWT token |

### Doctors
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctors` | List all doctors |
| GET | `/api/doctors/{id}` | Get doctor by ID |
| GET | `/api/doctors/available` | List available doctors |
| GET | `/api/doctors/specialty/{specialty}` | Filter by specialty |

### Appointments (Requires Auth)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/appointments` | Book appointment |
| GET | `/api/appointments/my` | Get my appointments |
| GET | `/api/appointments/upcoming` | Get upcoming appointments |
| DELETE | `/api/appointments/{id}` | Cancel appointment |

---

## 📁 Project Structure

```
healthtech-demo/
├── src/main/java/com/healthtech/
│   ├── controller/          # REST controllers
│   ├── service/             # Business logic
│   ├── repository/          # Data access layer
│   ├── entity/              # JPA entities
│   ├── dto/                 # Request/Response objects
│   ├── security/            # JWT & Spring Security
│   ├── exception/           # Custom exceptions
│   └── config/              # Configuration classes
├── src/main/resources/
│   ├── static/              # Frontend files
│   └── application.properties
├── frontend/                # Original frontend files
├── pom.xml                  # Maven configuration
└── README.md
```

---

## 🔐 Security

- **Password Hashing**: BCrypt with salt
- **JWT Tokens**: HMAC-SHA256 signed tokens
- **Stateless Sessions**: No server-side session storage
- **CORS**: Configured for frontend origins

---

## 📝 Sample Data

On startup, the application automatically seeds 6 sample doctors:
- Dr. Sarah Johnson (Cardiology)
- Dr. Michael Chen (Dermatology)
- Dr. Emily Williams (Pediatrics)
- Dr. James Rodriguez (Orthopedics)
- Dr. Amanda Thompson (Neurology)
- Dr. Robert Kim (General Medicine)

---

## 🧪 Testing the API

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'

# Get doctors
curl http://localhost:8080/api/doctors
```

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

---

## 👨‍💻 Author

**Yug** - [GitHub](https://github.com/yug005)

---

*Built with ❤️ using Spring Boot*
