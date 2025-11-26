package pdo.fraudshield.repository;

import pdo.fraudshield.entity.FraudAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FraudAnalysisRepository extends JpaRepository<FraudAnalysis, UUID> {

    Optional<FraudAnalysis> findByTransactionId(UUID transactionId);

    List<FraudAnalysis> findByStatus(FraudAnalysis.FraudStatus status);

    @Query("SELECT fa FROM FraudAnalysis fa WHERE fa.analyzedAt BETWEEN :start AND :end")
    List<FraudAnalysis> findAnalysesInPeriod(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(fa) FROM FraudAnalysis fa WHERE fa.status = :status")
    long countByStatus(@Param("status") FraudAnalysis.FraudStatus status);
}