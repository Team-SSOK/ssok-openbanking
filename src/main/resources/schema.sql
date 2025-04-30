-- MySQL 8.0 schema for SSOK OpenBanking

-- 트랜잭션 테이블
CREATE TABLE IF NOT EXISTS `transaction` (
  `transaction_id` VARCHAR(36) NOT NULL COMMENT '거래 고유 ID (UUID)',
  `send_account` VARCHAR(20) NOT NULL COMMENT '출금 계좌번호',
  `recv_account` VARCHAR(20) NOT NULL COMMENT '입금 계좌번호',
  `send_bank_code` VARCHAR(10) NOT NULL COMMENT '출금 은행코드',
  `recv_bank_code` VARCHAR(10) NOT NULL COMMENT '입금 은행코드',
  `amount` BIGINT NOT NULL COMMENT '송금액',
  `send_name` VARCHAR(100) NOT NULL COMMENT '출금자명',
  `rev_name` VARCHAR(100) NOT NULL COMMENT '입금자명',
  `status` VARCHAR(30) NOT NULL COMMENT '거래상태(REQUESTED, COMPLETED, FAILED 등)',
  `created_at` TIMESTAMP NOT NULL COMMENT '생성시간',
  `updated_at` TIMESTAMP NOT NULL COMMENT '수정시간',
  PRIMARY KEY (`transaction_id`),
  INDEX `idx_transaction_status` (`status`),
  INDEX `idx_transaction_send_account` (`send_account`),
  INDEX `idx_transaction_recv_account` (`recv_account`),
  INDEX `idx_transaction_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='송금 거래 내역';

-- 인덱스 추가
ALTER TABLE `transaction` ADD INDEX `idx_transaction_combined_1` (`send_account`, `status`, `created_at`);
ALTER TABLE `transaction` ADD INDEX `idx_transaction_combined_2` (`recv_account`, `status`, `created_at`);
