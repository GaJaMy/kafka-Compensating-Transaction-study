# Kafka 기반 분산 트랜잭션 학습 프로젝트

## 📚 프로젝트 개요

Apache Kafka를 활용한 **이벤트 기반 마이크로서비스 아키텍처**와 **Saga 패턴**을 학습하기 위한 프로젝트입니다.

실제 운영 환경에서 자주 사용되는 주문-재고-결제 시스템을 구현하며, 분산 환경에서의 트랜잭션 처리와 보상 트랜잭션(Compensating Transaction)을 직접 경험합니다.

## 🎯 학습 목표

### 1. Apache Kafka 핵심 개념
- **Topic**: 메시지를 분류하는 카테고리
- **Producer**: 이벤트를 발행하는 주체
- **Consumer**: 이벤트를 구독하는 주체
- **Consumer Group**: 메시지 분산 처리 및 브로드캐스트
- **Partition**: 토픽 내 데이터 분산 저장 단위

### 2. Event-Driven Architecture
- 서비스 간 느슨한 결합(Loose Coupling)
- 비동기 메시지 처리
- 이벤트 기반 통신

### 3. Saga Pattern (보상 트랜잭션)
- 분산 시스템에서의 트랜잭션 관리
- 각 단계별 성공/실패 처리
- 실패 시 이전 단계 롤백

### 4. 동시성 제어
- JPA Pessimistic Lock을 통한 재고 차감 동시성 처리

## 🛠 기술 스택

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.0 |
| Build Tool | Gradle (Multi-Module) |
| Messaging | Apache Kafka 7.5.0 |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Container | Docker Compose |

## 🏗 아키텍처

```
┌─────────────────┐      order-created       ┌──────────────────┐
│                 │─────────────────────────>│                  │
│  Order Service  │                          │ Inventory Service│
│    (Port: 8081) │<─────────────────────────│   (Port: 8082)   │
│                 │    inventory-reserved    │                  │
└────────┬────────┘                          └────────┬─────────┘
         │                                            │
         │ payment-completed                          │ order-failed
         │                                            │ (보상 트랜잭션)
         v                                            v
┌─────────────────┐    inventory-reserved    ┌──────────────────┐
│                 │<─────────────────────────│                  │
│ Payment Service │                          │  Kafka Broker    │
│   (Port: 8083)  │─────────────────────────>│  (Port: 9092)    │
│                 │      order-failed        │                  │
└─────────────────┘    (보상 트랜잭션)         └──────────────────┘
```

## 📁 프로젝트 구조

```
Kafka-Study/
├── order-service/          # 주문 관리 서비스
│   ├── domain/             # Order 엔티티
│   ├── dto/                # Request/Response/Event DTO
│   ├── repository/         # JPA Repository
│   ├── service/            # 비즈니스 로직
│   ├── controller/         # REST API
│   └── kafka/              # Producer & Consumer
│
├── inventory-service/      # 재고 관리 서비스
│   ├── domain/             # Inventory 엔티티 (price 필드 포함)
│   ├── dto/                # Response/Event DTO
│   ├── repository/         # Pessimistic Lock 적용
│   ├── service/            # 재고 차감 & 복구 로직
│   ├── controller/         # REST API
│   └── kafka/              # Producer & Consumer
│
├── payment-service/        # 결제 처리 서비스
│   ├── domain/             # Payment 엔티티
│   ├── dto/                # Response/Event DTO
│   ├── repository/         # JPA Repository
│   ├── service/            # 결제 처리 & 취소 로직
│   ├── controller/         # REST API
│   └── kafka/              # Producer & Consumer
│
├── docker-compose.yml      # Kafka & Zookeeper 설정
├── init-db.sql             # PostgreSQL 스키마 초기화
└── CLAUDE.md               # 상세 구현 가이드
```

## 🔄 Event Flow

### 정상 흐름 (Success Flow)

```
1. 주문 생성
   POST /orders → Order Service

2. 재고 예약
   order-created 이벤트 발행
   → Inventory Service 구독
   → 재고 충분성 검증 & 차감
   → inventory-reserved 이벤트 발행

3. 결제 처리
   inventory-reserved 이벤트 구독
   → Payment Service 처리
   → 결제 성공 (amount < 50,000원)
   → payment-completed 이벤트 발행

4. 주문 완료
   payment-completed 이벤트 구독
   → Order Service 상태 업데이트 (COMPLETED)
```

### 실패 흐름 (Failure Flow with Compensation)

```
시나리오 1: 재고 부족
   order-created → Inventory Service
   → 재고 부족 감지
   → order-failed 이벤트 발행
   → Order Service: 주문 상태 FAILED로 변경

시나리오 2: 결제 실패
   inventory-reserved → Payment Service
   → 결제 실패 (amount >= 50,000원)
   → order-failed 이벤트 발행
   → Inventory Service: 재고 복구 (보상 트랜잭션)
   → Order Service: 주문 상태 FAILED로 변경
```

## 🎓 주요 학습 포인트

### 1. Kafka Consumer Group 전략

**같은 groupId**: 로드 밸런싱

같은 groupId를 사용하면 메시지가 여러 Consumer 인스턴스에 분산되어 처리됩니다.

```java
// inventory-service-group-1과 inventory-service-group-2가
// 같은 groupId를 사용하면 한 메시지를 둘 중 하나만 처리 (부하 분산)
@KafkaListener(topics = "order-created", groupId = "inventory-service-group")
public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
    // 처리 로직
}
```

**다른 groupId**: 브로드캐스트

다른 groupId를 사용하면 모든 Consumer가 같은 메시지를 받습니다.

```java
// order-service-group과 inventory-service-group이 다르므로
// order-failed 이벤트를 둘 다 받아서 각자 보상 트랜잭션 처리
@KafkaListener(topics = "order-failed", groupId = "order-service-group")
public void handleOrderFailed(OrderFailedEvent event) {
    // Order Service의 처리
}

@KafkaListener(topics = "order-failed", groupId = "inventory-service-group")
public void handleOrderFailed(OrderFailedEvent event) {
    // Inventory Service의 처리
}
```

### 2. 보상 트랜잭션 설계

- **Inventory Service**: `order-failed` 구독 → 재고 복구
- **Payment Service**: `order-failed` 구독 **불필요** (결제가 마지막 단계이므로)

### 3. 동시성 제어

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
Optional<Inventory> findByProductIdWithLock(@Param("productId") Long productId);
```

동시에 같은 상품 주문 시 Race Condition 방지

### 4. PENDING 상태의 의미

외부 API 호출 전에 PENDING 상태를 먼저 저장하여 결제 시도 이력을 보존합니다.

```java
public void processPayment(Long orderId, Long userId, Integer amount) {
    // 결제 시도를 먼저 기록 (외부 API 호출 전)
    Payment payment = Payment.builder()
        .orderId(orderId)
        .userId(userId)
        .amount(amount)
        .status(PaymentStatus.PENDING)
        .build();
    paymentRepository.save(payment);

    try {
        // 외부 PG사 API 호출 시뮬레이션
        Thread.sleep(2000);

        if (amount >= 50000) {
            throw new RuntimeException("결제 한도 초과");
        }

        // 결제 성공
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);
        paymentProducer.sendPaymentCompletedEvent(orderId, payment.getPaymentId());
    } catch (Exception e) {
        // 결제 실패
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        paymentProducer.sendOrderFailedEvent(orderId, e.getMessage());
    }
}
```

서버 장애 시에도 결제 시도 이력을 보존하여 재처리 가능

### 5. DTO 패턴

- **API Layer**: RequestDto, ResponseDto
- **Event Layer**: Event DTO (Kafka 메시지)
- **Domain Layer**: Entity (JPA)

각 레이어 분리로 계약 변경 최소화

## 🚀 실행 방법

### 1. 환경 준비

```bash
# Docker로 Kafka 실행
docker-compose up -d

# PostgreSQL 스키마 생성
psql -U postgres -f init-db.sql
```

### 2. 환경 변수 설정

```bash
# Windows (PowerShell)
$env:DB_PASSWORD="postgres"

# macOS/Linux
export DB_PASSWORD="postgres"
```

### 3. 애플리케이션 실행

```bash
# 각 서비스를 별도 터미널에서 실행
./gradlew :order-service:bootRun
./gradlew :inventory-service:bootRun
./gradlew :payment-service:bootRun
```

### 4. 초기 데이터 삽입

```sql
-- PostgreSQL에 연결 후 실행
SET search_path TO inventory_db;

INSERT INTO inventory (product_id, quantity, price) VALUES (1, 100, 10000);
INSERT INTO inventory (product_id, quantity, price) VALUES (2, 50, 25000);
INSERT INTO inventory (product_id, quantity, price) VALUES (3, 200, 5000);
```

### 5. 테스트

#### 성공 케이스 (결제 금액 < 50,000원)
```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 3,
    "userId": 100
  }'
```

#### 실패 케이스 (결제 금액 >= 50,000원)
```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 2,
    "quantity": 2,
    "userId": 100
  }'
```

## 📊 데이터베이스 스키마

### Order Service (order_db)
```sql
orders (
  order_id BIGSERIAL PRIMARY KEY,
  product_id BIGINT,
  quantity INTEGER,
  user_id BIGINT,
  status VARCHAR(20)  -- PENDING, COMPLETED, FAILED
)
```

### Inventory Service (inventory_db)
```sql
inventory (
  product_id BIGINT PRIMARY KEY,
  quantity INTEGER,
  price INTEGER
)
```

### Payment Service (payment_db)
```sql
payments (
  payment_id BIGSERIAL PRIMARY KEY,
  order_id BIGINT,
  user_id BIGINT,
  amount INTEGER,
  status VARCHAR(20)  -- PENDING, COMPLETED, FAILED
)
```

## 🔍 Kafka Topics

| Topic | Producer | Consumer | 설명 |
|-------|----------|----------|------|
| `order-created` | Order Service | Inventory Service | 주문 생성 알림 |
| `inventory-reserved` | Inventory Service | Order Service, Payment Service | 재고 예약 완료 |
| `payment-completed` | Payment Service | Order Service | 결제 완료 |
| `order-failed` | Inventory Service, Payment Service | Order Service, Inventory Service | 주문 실패 (보상 트랜잭션) |

## 💡 개선 아이디어

- [ ] Kafka Partition Key 전략 (orderId 기반)
- [ ] Dead Letter Queue (DLQ) 패턴
- [ ] Idempotent Consumer (멱등성 보장)
- [ ] Saga Orchestration vs Choreography 비교
- [ ] Outbox Pattern (트랜잭션 일관성)
- [ ] Event Sourcing 적용

## 📝 참고 문서

- [CLAUDE.md](CLAUDE.md): 상세 구현 가이드 및 TODO 주석 설명
- [DB-SETUP.md](DB-SETUP.md): 데이터베이스 설정 가이드
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Kafka Reference](https://docs.spring.io/spring-kafka/reference/)

## 🎖 학습 성과

이 프로젝트를 통해 다음을 학습했습니다:

1. ✅ Kafka를 활용한 이벤트 기반 아키텍처 구현
2. ✅ 분산 시스템에서의 트랜잭션 관리 (Saga Pattern)
3. ✅ 보상 트랜잭션을 통한 데이터 일관성 유지
4. ✅ Consumer Group을 활용한 메시지 분산 처리
5. ✅ 멀티 모듈 프로젝트 구성 및 서비스 간 통신
6. ✅ JPA Pessimistic Lock을 통한 동시성 제어
7. ✅ DTO 패턴을 활용한 계층 분리

---

**작성자**: GaJaMy  
**목적**: Apache Kafka 학습 및 이벤트 기반 마이크로서비스 아키텍처 실습  
**기간**: 2025년
