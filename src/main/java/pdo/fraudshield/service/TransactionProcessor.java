package pdo.fraudshield.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pdo.fraudshield.domain.FraudAnalysis;
import pdo.fraudshield.domain.Transaction;
import pdo.fraudshield.repository.FraudAnalysisRepository;
import pdo.fraudshield.repository.TransactionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProcessor {

    private final TransactionRepository transactionRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;

    public FraudAnalysis processTransaction(Transaction transaction) {
        log.info("Processing transaction: {} for customer: {}",
                transaction.getId(), transaction.getCustomerId());

        Transaction savedTransaction = transactionRepository.save(transaction);

        //TODO: Basic fraud analysis (placeholder - will be replaced with real math)
        FraudAnalysis analysis = performBasicAnalysis(savedTransaction);

        return fraudAnalysisRepository.save(analysis);
    }

    private FraudAnalysis performBasicAnalysis(Transaction transaction) {
        // TODO: Temporary basic logic - will be replaced with mathematical models
        boolean isSuspicious = isTransactionSuspicious(transaction);

        return FraudAnalysis.builder()
                .transactionId(transaction.getId())
                .customerId(transaction.getCustomerId())
                .status(isSuspicious ?
                        FraudAnalysis.FraudStatus.MANUAL_REVIEW :
                        FraudAnalysis.FraudStatus.APPROVED)
                .riskScore(isSuspicious ? java.math.BigDecimal.valueOf(75) : java.math.BigDecimal.valueOf(10))
                .confidenceLevel(java.math.BigDecimal.valueOf(0.85))
                .analyzedAt(java.time.LocalDateTime.now())
                .build();
    }

    private boolean isTransactionSuspicious(Transaction transaction) {
        //TODO: will be replaced with mathematical models
        return transaction.getAmount().compareTo(java.math.BigDecimal.valueOf(5000)) > 0 ||
                isNightTime(transaction.getTimestamp());
    }

    private boolean isNightTime(java.time.LocalDateTime timestamp) {
        int hour = timestamp.getHour();
        return hour < 6 || hour > 22;
    }
}