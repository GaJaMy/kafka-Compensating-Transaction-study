# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 4.0.0 + Java 21 multi-module learning project for Apache Kafka integration in a microservices architecture.

**Purpose:** Implement an event-driven architecture with three services (Order, Inventory, Payment) communicating asynchronously via Kafka.

## Multi-Module Structure

This is a Gradle multi-module project with three independent services:

```
Kafka-Study/
├── order-service/       (Port 8081)
├── inventory-service/   (Port 8082)
├── payment-service/     (Port 8083)
└── docker-compose.yml   (Kafka + Zookeeper)
```

## Build and Development Commands

### Start Kafka Infrastructure
```bash
docker-compose up -d
```

### Stop Kafka Infrastructure
```bash
docker-compose down
```

### Build All Modules
```bash
./gradlew build
```

### Build Specific Module
```bash
./gradlew :order-service:build
./gradlew :inventory-service:build
./gradlew :payment-service:build
```

### Run Specific Service
```bash
./gradlew :order-service:bootRun
./gradlew :inventory-service:bootRun
./gradlew :payment-service:bootRun
```

### Run All Tests
```bash
./gradlew test
```

### Clean Build
```bash
./gradlew clean build
```

## Technology Stack

- **Spring Boot:** 4.0.0
- **Java:** 21 (toolchain configured)
- **Build Tool:** Gradle 9.2.1 with Gradle Wrapper
- **Messaging:** Apache Kafka (via Docker)
- **Database:** PostgreSQL (Local)
- **Dependencies (per module):**
  - `spring-boot-starter-web` - REST API
  - `spring-boot-starter-data-jpa` - JPA/Hibernate
  - `spring-kafka` - Kafka integration
  - `postgresql` - PostgreSQL driver
  - `lombok` - Boilerplate reduction
  - JUnit 5 - Testing framework

## Database Architecture

### PostgreSQL Schema Structure

Each service has its own isolated database schema following microservices best practices:

```
PostgreSQL (localhost:5432)
├── order_db schema
│   └── orders table (order_id, product_id, quantity, user_id, status)
├── inventory_db schema
│   └── inventory table (product_id, quantity)
└── payment_db schema
    └── payments table (payment_id, order_id, user_id, amount, status)
```

### Database Setup

1. **Initialize schemas:**
   ```bash
   psql -U postgres -f init-db.sql
   ```

2. **Update passwords:**
   - Edit `application.yml` in each service
   - Change `spring.datasource.password` to your PostgreSQL password

3. **Tables auto-created by JPA:**
   - `ddl-auto: update` creates tables automatically on first run

4. **Initial inventory data (optional):**
   ```sql
   SET search_path TO inventory_db;
   INSERT INTO inventory (product_id, quantity) VALUES (1, 100);
   INSERT INTO inventory (product_id, quantity) VALUES (2, 50);
   INSERT INTO inventory (product_id, quantity) VALUES (3, 200);
   ```

See `DB-SETUP.md` for detailed setup instructions.

### Concurrency Control

**inventory-service** uses **pessimistic locking** to prevent race conditions:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
Optional<Inventory> findByProductIdWithLock(@Param("productId") Long productId);
```

This ensures atomic inventory updates when multiple orders arrive simultaneously.

## Architecture

Event-driven order processing system with Saga pattern for distributed transactions:

```
Order Service (8081)
    ↓ publishes: order-created
Kafka Broker (9092)
    ↓
    ├─→ Inventory Service (8082)
    │       ↓ publishes: inventory-reserved or order-failed
    │
    └─→ Payment Service (8083)
            ↓ publishes: payment-completed or order-failed
```

### Kafka Topics
- `order-created` - New order events
- `inventory-reserved` - Stock reservation success
- `payment-completed` - Payment success
- `order-failed` - Order rollback events (compensation transactions)

### Service Details

**order-service (Port 8081)**
- Publishes: `order-created`
- Consumes: `inventory-reserved`, `payment-completed`, `order-failed`
- Package: `com.example.order`
- DB Schema: `order_db`

**inventory-service (Port 8082)**
- Publishes: `inventory-reserved`, `order-failed`
- Consumes: `order-created`, `order-failed`
- Package: `com.example.inventory`
- DB Schema: `inventory_db`

**payment-service (Port 8083)**
- Publishes: `payment-completed`, `order-failed`
- Consumes: `inventory-reserved`, `order-failed`
- Package: `com.example.payment`
- DB Schema: `payment_db`

### Saga Pattern - Compensation Transactions

Each service can both **publish** and **consume** `order-failed` events:

- **Publishes `order-failed`:** When its own operation fails (e.g., inventory shortage, payment decline)
- **Consumes `order-failed`:** To rollback its changes when another service fails

**Example Flow:**
```
1. Order created ✅ → DB saved
2. Inventory reserved ✅ → quantity decremented
3. Payment fails ❌ → payment-service publishes order-failed
4. inventory-service consumes order-failed → rollback (quantity restored)
5. order-service consumes order-failed → status updated to FAILED
```

## Module Structure (Each Service)

```
{service-name}/
├── src/main/java/com/example/{service}/
│   ├── {Service}Application.java       - Main entry point
│   ├── domain/                         - JPA Entity classes (@Entity)
│   ├── dto/                            - Data Transfer Objects
│   │   ├── *RequestDto.java            - API request DTOs
│   │   ├── *ResponseDto.java           - API response DTOs
│   │   └── event/                      - Kafka event DTOs
│   │       ├── OrderCreatedEvent.java
│   │       ├── InventoryReservedEvent.java
│   │       ├── PaymentCompletedEvent.java
│   │       └── OrderFailedEvent.java
│   ├── repository/                     - JPA Repository interfaces
│   ├── service/                        - Business logic (TODO methods)
│   ├── controller/                     - REST endpoints (TODO methods)
│   └── kafka/                          - Producer/Consumer (TODO methods)
└── src/main/resources/
    ├── application.yml                 - Service & DB configuration
    └── data.sql (optional)             - Initial data
```

## DTO Pattern

This project uses **DTO (Data Transfer Object)** pattern to separate API contracts from domain models:

### API DTOs (Controller Layer)

**Request DTOs** - Accept data from REST clients:
- `OrderRequestDto` - Create order request (productId, quantity, userId)

**Response DTOs** - Return data to REST clients:
- `OrderResponseDto` - Order information with status
- `InventoryResponseDto` - Product inventory information
- `PaymentResponseDto` - Payment information with status

### Event DTOs (Kafka Layer)

**Event DTOs** - Messages exchanged via Kafka topics:
- `OrderCreatedEvent` - Published by order-service
- `InventoryReservedEvent` - Published by inventory-service
- `PaymentCompletedEvent` - Published by payment-service
- `OrderFailedEvent` - Published by any service on failure

### DTO Conversion Flow

```
REST Request (JSON)
    ↓
RequestDto (@RequestBody)
    ↓
Service Layer → converts to Entity
    ↓
Repository.save(Entity) → DB persistence
    ↓
Service Layer → converts Entity to ResponseDto
    ↓
ResponseDto (@ResponseBody) → JSON response
```

**Benefits:**
- Decouples API contracts from database schema
- Prevents exposing internal entity structure
- Allows independent evolution of API and domain models

## Implementation Status

**Completed:**
- ✅ Multi-module Gradle setup (3 services)
- ✅ JPA Entity classes with proper annotations
- ✅ Repository interfaces with pessimistic locking
- ✅ DTO pattern (Request/Response/Event DTOs)
- ✅ Skeleton Service classes with detailed TODO comments
- ✅ Controller classes using DTO pattern
- ✅ Kafka Producer/Consumer skeleton with @KafkaListener
- ✅ PostgreSQL database configuration (3 schemas)
- ✅ Docker Compose for Kafka/Zookeeper
- ✅ Database initialization scripts

**TODO (Study Implementation):**
All methods in Service, Controller, Producer, and Consumer classes contain detailed `TODO` comments and throw `UnsupportedOperationException`. Implement step-by-step:

1. **Database Operations:**
   - DTO ↔ Entity conversions
   - CRUD operations using JpaRepository
   - Transaction management with @Transactional

2. **Kafka Producer Logic:**
   - Send events using kafkaTemplate.send()
   - Error handling for message delivery
   - Logging for debugging

3. **Kafka Consumer Logic:**
   - Process incoming events
   - Call service methods with parsed data
   - Handle deserialization

4. **Business Logic:**
   - Order creation and status updates
   - Inventory reservation and rollback
   - Payment processing and cancellation

5. **Saga Pattern:**
   - Compensation transactions (rollback on failure)
   - Event-driven state transitions
   - Distributed transaction coordination

6. **Testing:**
   - End-to-end flow testing
   - Failure scenario testing (inventory shortage, payment failure)
   - Verify compensation transactions work correctly

## Configuration Notes

Each service's `application.yml` includes:

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres?currentSchema={service}_db
    username: postgres
    password: postgres  # Change this!
  jpa:
    hibernate:
      ddl-auto: update  # Auto-creates tables
    show-sql: true      # Logs SQL queries
```

### Kafka Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: StringSerializer
      value-serializer: JsonSerializer  # Auto-converts DTOs to JSON
    consumer:
      group-id: {service}-group
      key-deserializer: StringDeserializer
      value-deserializer: JsonDeserializer  # Auto-converts JSON to DTOs
      properties:
        spring.json.trusted.packages: "*"  # Allows DTO deserialization
```

### Server Ports
- order-service: `8081`
- inventory-service: `8082`
- payment-service: `8083`

## Getting Started

### 1. Setup PostgreSQL
```bash
# Install PostgreSQL (if not installed)
# Create schemas
psql -U postgres -f init-db.sql

# Update application.yml passwords in all services
# Change: spring.datasource.password=postgres (to your password)
```

### 2. Start Kafka
```bash
docker-compose up -d

# Verify Kafka is running
docker-compose ps
```

### 3. Add Initial Inventory Data (Optional)
```sql
psql -U postgres
SET search_path TO inventory_db;
INSERT INTO inventory (product_id, quantity) VALUES (1, 100), (2, 50), (3, 200);
```

### 4. Run Services
Open 3 terminals and run each service:
```bash
# Terminal 1
./gradlew :order-service:bootRun

# Terminal 2
./gradlew :inventory-service:bootRun

# Terminal 3
./gradlew :payment-service:bootRun
```

### 5. Test the Flow

**Create an Order:**
```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 5,
    "userId": 100
  }'
```

**Check Order Status:**
```bash
curl http://localhost:8081/orders/{orderId}
```

**Check Inventory:**
```bash
curl http://localhost:8082/inventory/1
```

### 6. Implement TODO Methods

Follow the TODO comments in each class to implement:
1. Start with **OrderService.createOrder()** and **OrderProducer**
2. Then **InventoryConsumer** and **InventoryService.reserveInventory()**
3. Then **PaymentConsumer** and **PaymentService.processPayment()**
4. Finally, implement compensation transactions (order-failed handling)

### 7. Test Failure Scenarios

- Order product with insufficient inventory
- Simulate payment failure
- Verify compensation transactions (inventory rollback)