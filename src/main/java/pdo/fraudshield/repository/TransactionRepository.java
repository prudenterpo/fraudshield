package pdo.fraudshield.repository;

import pdo.fraudshield.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByCustomerId(UUID customerId);

    List<Transaction> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE t.amount > :minAmount")
    List<Transaction> findHighValueTransactions(@Param("minAmount") BigDecimal minAmount);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.customerId = :customerId")
    long countByCustomer(@Param("customerId") UUID customerId);
}