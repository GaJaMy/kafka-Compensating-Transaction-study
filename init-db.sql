-- Kafka Study 프로젝트 DB 초기화 스크립트
-- PostgreSQL에서 실행하세요: psql -U postgres -f init-db.sql

-- 스키마 생성
CREATE SCHEMA IF NOT EXISTS order_db;
CREATE SCHEMA IF NOT EXISTS inventory_db;
CREATE SCHEMA IF NOT EXISTS payment_db;

-- 스키마 권한 부여 (필요시)
GRANT ALL ON SCHEMA order_db TO postgres;
GRANT ALL ON SCHEMA inventory_db TO postgres;
GRANT ALL ON SCHEMA payment_db TO postgres;

-- inventory_db에 초기 재고 데이터 삽입 (선택사항)
-- 참고: JPA가 자동으로 테이블을 생성하므로, 애플리케이션 실행 후 데이터를 삽입해야 합니다.
-- 또는 애플리케이션 실행 후 아래 스크립트를 실행하세요.

/*
-- 애플리케이션 실행 후 실행할 초기 데이터 삽입 스크립트
SET search_path TO inventory_db;

INSERT INTO inventory (product_id, quantity) VALUES (1, 100);
INSERT INTO inventory (product_id, quantity) VALUES (2, 50);
INSERT INTO inventory (product_id, quantity) VALUES (3, 200);
*/

-- 스키마 확인
SELECT schema_name FROM information_schema.schemata WHERE schema_name LIKE '%_db';
