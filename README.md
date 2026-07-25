# 🔔 Event Driven Notification Service

> A high-throughput, fault-tolerant, event-driven distributed notification platform built with **Spring Boot 4**, **Apache Kafka**, **Redis**, and **MySQL**, implementing the **Transactional Outbox Pattern** and **Dead Letter Queue (DLQ)**.

---

## 🌟 Key Architecture & Highlights

```
                       ┌─────────────────────────────────────────────────┐
                       │           Client / External System              │
                       └───────────────────────┬─────────────────────────┘
                                               │ POST /api/events/send
                                               ▼
                       ┌─────────────────────────────────────────────────┐
                       │          Spring Boot REST API Controller        │
                       └───────────────────────┬─────────────────────────┘
                                               │ Publish Ingestion Event
                                               ▼
                       ┌─────────────────────────────────────────────────┐
                       │        Kafka Topic: notification-events         │
                       └───────────────────────┬─────────────────────────┘
                                               │ Consumed by Worker
                                               ▼
                       ┌─────────────────────────────────────────────────┐
                       │   Notification Engine (Redis Deduplication/RL)  │
                       └───────────────────────┬─────────────────────────┘
                                               │ Atomically Writes Entity & Outbox
                                               ▼
                       ┌─────────────────────────────────────────────────┐
                       │   MySQL Database (Outbox + Notification Tables) │
                       └───────────────────────┬─────────────────────────┘
                                               │ Outbox Scheduler (Poller)
                                               ▼
                       ┌─────────────────────────────────────────────────┐
                       │          Kafka Topic: delivery-events           │
                       └───────────────────────┬─────────────────────────┘
                                               │ Consumed by Delivery Worker
                                               ▼
                       ┌─────────────────────────────────────────────────┐
                       │   Multi-Channel Providers (Brevo Email, Twilio) │
                       └─────────────────────────────────────────────────┘
```

* **Transactional Outbox Pattern**: Decouples database transactions from message publishing, eliminating the "Dual-Write" problem and guaranteeing **At-Least-Once Delivery**.
* **Atomic Idempotency & Rate Limiting**: Uses Redis `setIfAbsent` (24-hour TTL) for instant duplicate suppression and a sliding-window rate limiter per user.
* **Self-Healing Failure Recovery**: Automated background schedulers reset stuck tasks (`PROCESSING` > 5 minutes) and retry failed deliveries exponentially.
* **Dead Letter Queue (DLQ)**: Permanently failed notifications exceed max retries are routed to the `delivery-events-dlq` topic and archived in database tables without blocking workers.
* **Stateless Security**: Spring Security + JWT authentication featuring Redis-backed token revocation/blacklisting.
* **Production Observability**: Spring Boot Actuator health probes (`/actuator/health`), metrics, and structured SLF4J logging.

---

## 🛠️ Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Framework** | Spring Boot 4.x / Java 25 | Core application framework & dependency injection |
| **Messaging** | Apache Kafka (KRaft mode) | Distributed event streaming broker |
| **Database** | MySQL 8 | Relational storage for entities, deliveries, and outbox logs |
| **Caching** | Redis | In-memory cache for idempotency, rate limiting & token revocation |
| **Security** | Spring Security & JWT | Stateless authorization and authentication |
| **Email Provider** | Brevo (Sendinblue) SMTP Relay | Production transactional email dispatching |
| **SMS Provider** | Twilio Java SDK | Production virtual SMS gateway |
| **Containerization** | Docker & Docker Compose | Container orchestration for full-stack environments |

---

## 🚀 Quick Start

### 1. Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed.
* Java 25 & Maven 3.9+ (if running locally outside Docker).

### 2. Environment Configuration
Create a local `.env` file in the root directory (copied from `.env.example`):

```bash
cp .env.example .env
```

Configure your local credentials inside `.env`:
```env
# Database Credentials
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=notification_service_directory
MYSQL_USER=springstudent
MYSQL_PASSWORD=springstudent

# Email Credentials (Brevo SMTP)
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=your_brevo_smtp_username
MAIL_PASSWORD=your_brevo_smtp_password
MAIL_FROM=your_verified_sender_email@gmail.com

# SMS Credentials (Twilio)
SMS_PROVIDER=TWILIO
TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token
TWILIO_PHONE_NUMBER=+1234567890
```

---

## 🐳 Running with Docker

### Mode A: Full Stack Mode (Recommended)
Spins up **MySQL**, **Redis**, **Kafka (KRaft)**, and the **Notification Application** containerized:

```bash
# Build and launch all containers
docker compose up --build -d

# View container logs
docker compose logs -f notification-service
```

Access the Application:
* **Base URL**: `http://localhost:8080`
* **MySQL Database**: `localhost:3307`
* **Kafka Broker**: `localhost:9092`
* **Redis Cache**: `localhost:6379`

### Mode B: Infrastructure Only (Development Mode)
Spins up MySQL, Redis, and Kafka in Docker while allowing you to run Spring Boot inside your IDE:

```bash
docker compose -f docker-compose.dev.yml up -d
```

---

## 📡 API Reference

### 🔐 Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user | No |
| `POST` | `/api/auth/login` | Authenticate user & receive JWT | No |
| `POST` | `/api/auth/refresh` | Obtain new access token via refresh token | No |
| `POST` | `/api/auth/logout` | Revoke JWT token (Blacklist in Redis) | Yes |

### 📩 Event & Preference Endpoints

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/events/send` | Ingest notification event to Kafka | Yes |
| `GET` | `/api/preferences` | Get authenticated user's channel preferences | Yes |
| `PUT` | `/api/preferences` | Update notification channel preferences | Yes |

### 📊 Admin Analytics Dashboard

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/dashboard` | System-wide delivery stats & channel metrics | Yes (`ROLE_ADMIN`) |
| `GET` | `/api/admin/dashboard/channel/{channel}` | Channel-specific delivery breakdown | Yes (`ROLE_ADMIN`) |
| `GET` | `/api/admin/notifications/{id}` | Detailed notification payload & delivery logs | Yes (`ROLE_ADMIN`) |
| `GET` | `/api/admin/notifications` | Paginated & filtered delivery query endpoint | Yes (`ROLE_ADMIN`) |

### 🩺 Actuator Observability

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/actuator/health` | Service liveness & component status (DB, Disk, Redis) |
| `GET` | `/actuator/metrics` | System runtime metrics |
| `GET` | `/actuator/info` | Application metadata |

---

## 🧪 Usage Example (cURL)

### 1. Ingest Notification Event
```bash
curl -X POST http://localhost:8080/api/events/send \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": "evt-9901",
    "userId": 1,
    "type": "PAYMENT_FAILED",
    "message": "Your subscription payment of $19.99 failed."
  }'
```

### 2. Query System Health
```bash
curl http://localhost:8080/actuator/health
```

---

## 🛡️ License

Distributed under the MIT License. See `LICENSE` for more information.
