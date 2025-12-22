# Database Setup Guide

## PostgreSQL 설치 및 설정

### 1. PostgreSQL 설치

**Windows:**
- [PostgreSQL 공식 사이트](https://www.postgresql.org/download/windows/)에서 설치
- 기본 포트: 5432
- 기본 사용자: postgres
- 비밀번호: 설치 시 설정한 비밀번호

**Mac (Homebrew):**
```bash
brew install postgresql
brew services start postgresql
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### 2. 스키마 생성

프로젝트 루트의 `init-db.sql` 파일을 실행하여 3개의 스키마를 생성합니다.

```bash
# psql 명령어로 실행
psql -U postgres -f init-db.sql

# 또는 PostgreSQL 클라이언트(pgAdmin, DBeaver 등)에서 실행
```

생성되는 스키마:
- `order_db` - 주문 서비스용
- `inventory_db` - 재고 서비스용
- `payment_db` - 결제 서비스용

### 3. application.yml 설정 확인

각 서비스의 `application.yml` 파일에서 DB 연결 정보를 확인하세요:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres?currentSchema=order_db
    username: postgres
    password: postgres  # 실제 비밀번호로 변경 필요
```

**중요:** `password` 값을 실제 PostgreSQL 비밀번호로 변경하세요!

### 4. 테이블 자동 생성

Spring Boot 애플리케이션을 실행하면 JPA가 자동으로 테이블을 생성합니다 (`ddl-auto: update` 설정).

생성되는 테이블:
- `order_db.orders`
- `inventory_db.inventory`
- `payment_db.payments`

### 5. 초기 재고 데이터 삽입 (선택사항)

애플리케이션 실행 후, 테스트용 재고 데이터를 삽입할 수 있습니다:

```sql
SET search_path TO inventory_db;

INSERT INTO inventory (product_id, quantity) VALUES (1, 100);
INSERT INTO inventory (product_id, quantity) VALUES (2, 50);
INSERT INTO inventory (product_id, quantity) VALUES (3, 200);
```

## DB 연결 확인

각 서비스를 실행하면 로그에서 다음과 같은 메시지를 확인할 수 있습니다:

```
Hibernate: create table orders (...)
HikariPool-1 - Start completed.
```

## 문제 해결

### 연결 실패 시 체크리스트:
1. PostgreSQL 서비스가 실행 중인가?
2. `application.yml`의 비밀번호가 올바른가?
3. 스키마가 생성되었는가?
4. 방화벽에서 5432 포트가 열려있는가?

### 스키마 확인:
```sql
SELECT schema_name FROM information_schema.schemata WHERE schema_name LIKE '%_db';
```

### 테이블 확인:
```sql
SET search_path TO order_db;
\dt  -- psql에서
-- 또는
SELECT * FROM information_schema.tables WHERE table_schema = 'order_db';
```
