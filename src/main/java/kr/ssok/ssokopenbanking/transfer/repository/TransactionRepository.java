package kr.ssok.ssokopenbanking.transfer.repository;

import kr.ssok.ssokopenbanking.transfer.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
