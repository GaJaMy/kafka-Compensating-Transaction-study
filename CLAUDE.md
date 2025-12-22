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
- **Dependencies (per module):**
  - `spring-boot-starter-web` - REST API
  - `spring-kafka` - Kafka integration
  - `lombok` - Boilerplate reduction
  - JUnit 5 - Testing framework

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
- Consumes: `inventory-reserved`, `payment-completed`
- Package: `com.example.order`

**inventory-service (Port 8082)**
- Publishes: `inventory-reserved`, `order-failed`
- Consumes: `order-created`
- Package: `com.example.inventory`

**payment-service (Port 8083)**
- Publishes: `payment-completed`, `order-failed`
- Consumes: `inventory-reserved`
- Package: `com.example.payment`

## Module Structure (Each Service)

```
{service-name}/
├── src/main/java/com/example/{service}/
│   ├── {Service}Application.java       - Main entry point
│   ├── domain/                         - Domain models
│   ├── service/                        - Business logic (TODO methods)
│   ├── controller/                     - REST endpoints (TODO methods)
│   └── kafka/                          - Producer/Consumer (TODO methods)
└── src/main/resources/
    └── application.yml                 - Service configuration
```

## Implementation Status

**Completed:**
- Multi-module Gradle setup
- Skeleton classes with method signatures
- Domain models and enums
- Kafka configuration templates
- Docker Compose for Kafka/Zookeeper

**TODO (Study Implementation):**
All methods in Service, Controller, Producer, and Consumer classes contain `TODO` comments and throw `UnsupportedOperationException`. These need to be implemented as part of learning Kafka:

1. Implement Kafka Producer logic (send events)
2. Implement Kafka Consumer logic (receive and process events)
3. Implement business logic in Service classes
4. Implement REST endpoints in Controller classes
5. Test event flow across services

## Configuration Notes

Each service's `application.yml` includes:
- Kafka bootstrap server: `localhost:9092`
- JSON serialization/deserialization
- Unique server ports (8081, 8082, 8083)
- Service-specific consumer group IDs

## Getting Started

1. Start Kafka: `docker-compose up -d`
2. Run each service: `./gradlew :{service-name}:bootRun`
3. Implement TODO methods to learn Kafka messaging
4. Test with REST endpoints (e.g., `POST http://localhost:8081/orders`)