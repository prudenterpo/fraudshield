package pdo.fraudshield.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pdo.fraudshield.domain.FraudAnalysis;
import pdo.fraudshield.domain.Transaction;
import pdo.fraudshield.math.RuleEngine;
import pdo.fraudshield.repository.FraudAnalysisRepository;
import pdo.fraudshield.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProcessor {

    private final TransactionRepository transactionRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;
    private final RuleEngine ruleEngine;

    public FraudAnalysis processTransaction(Transaction transaction) {
        log.info("Processing transaction: {} for customer: {}", transaction.getId(), transaction.getCustomerId());

        Transaction savedTransaction = transactionRepository.save(transaction);

        //TODO: Basic fraud analysis (placeholder - will be replaced with real math)
        FraudAnalysis analysis = performBasicAnalysis(savedTransaction);

        return fraudAnalysisRepository.save(analysis);
    }

    private FraudAnalysis performBasicAnalysis(Transaction transaction) {
        Map<String, Object> ruleResults = ruleEngine.evaluateRules(transaction);
        int discreteRiskScore = (int) ruleResults.get("discreteRiskScore");

        FraudAnalysis.FraudStatus status = determineStatus(discreteRiskScore);

        return FraudAnalysis.builder()
                .transactionId(transaction.getId())
                .customerId(transaction.getCustomerId())
                .status(status)
                .riskScore(BigDecimal.valueOf(discreteRiskScore))
                .confidenceLevel(calculateConfidence(ruleResults))
                .analyzedAt(LocalDateTime.now())
                .build();
    }

    private FraudAnalysis.FraudStatus determineStatus(int riskScore) {
        if (riskScore >= 90) return FraudAnalysis.FraudStatus.REJECTED;
        if (riskScore >= 70) return FraudAnalysis.FraudStatus.MANUAL_REVIEW;
        return FraudAnalysis.FraudStatus.APPROVED;
    }

    private BigDecimal calculateConfidence(Map<String, Object> ruleResults) {
        int triggeredRules = (int) ruleResults.get("triggeredRules");
        // More rules triggered = higher confidence in the analysis
        double confidence = 0.7 + (triggeredRules * 0.1);
        return BigDecimal.valueOf(Math.min(confidence, 0.95));
    }
}