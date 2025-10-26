package pdo.fraudshield.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pdo.fraudshield.domain.FraudAnalysis;
import pdo.fraudshield.domain.Transaction;
import pdo.fraudshield.math.GradientOptimizer;
import pdo.fraudshield.math.ProbabilityEngine;
import pdo.fraudshield.math.RuleEngine;
import pdo.fraudshield.math.SimilarityAnalyzer;
import pdo.fraudshield.repository.FraudAnalysisRepository;
import pdo.fraudshield.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProcessor {

    private final TransactionRepository transactionRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;
    private final RuleEngine ruleEngine;
    private final SimilarityAnalyzer similarityAnalyzer;
    private final GradientOptimizer gradientOptimizer;
    private final ProbabilityEngine probabilityEngine;

    public FraudAnalysis processTransaction(Transaction transaction) {
        log.info("Processing transaction: {} for customer: {}",
                transaction.getId(), transaction.getCustomerId());

        Transaction savedTransaction = transactionRepository.save(transaction);
        FraudAnalysis analysis = performBasicAnalysis(savedTransaction);
        return fraudAnalysisRepository.save(analysis);
    }

    private FraudAnalysis performBasicAnalysis(Transaction transaction) {
        // 1. Mathematics Discrete - Rule Engine
        Map<String, Object> ruleResults = ruleEngine.evaluateRules(transaction);
        int discreteRiskScore = (int) ruleResults.get("discreteRiskScore");

        // 2. Algebra Linear - Similarity Analysis
        Map<String, Object> similarityResults = similarityAnalyzer.analyzeSimilarity(transaction);
        double anomalyScore = (double) similarityResults.get("anomalyScore");

        // 3. Statistics - Bayesian Probability
        Map<String, Object> probabilityResults = probabilityEngine.calculateProbabilities(transaction);
        int bayesianScore = (int) probabilityResults.get("bayesianScore");

        // 4. Combine all three mathematical approaches
        int finalRiskScore = calculateCombinedRiskScore(discreteRiskScore, anomalyScore, bayesianScore);
        FraudAnalysis.FraudStatus status = determineStatus(finalRiskScore);

        return FraudAnalysis.builder()
                .transactionId(transaction.getId())
                .customerId(transaction.getCustomerId())
                .status(status)
                .riskScore(BigDecimal.valueOf(finalRiskScore))
                .confidenceLevel(calculateConfidence(ruleResults, similarityResults, probabilityResults))
                .analyzedAt(LocalDateTime.now())
                .build();
    }

    private int calculateCombinedRiskScore(int discreteScore, double anomalyScore, int bayesianScore) {
        double[] currentWeights = gradientOptimizer.getCurrentWeights();

        // Use optimized weights from calculus
        double ruleScoreNormalized = discreteScore / 100.0;
        double similarityScoreNormalized = anomalyScore;
        double bayesianScoreNormalized = bayesianScore / 100.0;

        // For now, equal weights for all three components
        double combinedScore = currentWeights[0] * ruleScoreNormalized +
                currentWeights[1] * similarityScoreNormalized +
                0.33 * bayesianScoreNormalized;

        return (int) (combinedScore * 100);
    }

    private BigDecimal calculateConfidence(Map<String, Object> ruleResults,
                                           Map<String, Object> similarityResults,
                                           Map<String, Object> probabilityResults) {
        int triggeredRules = (int) ruleResults.get("triggeredRules");
        int comparisonCount = (int) similarityResults.get("comparisonCount");
        boolean statisticallySignificant = (boolean) probabilityResults.get("statisticallySignificant");

        double rulesConfidence = 0.4 + (triggeredRules * 0.1);
        double dataConfidence = Math.min(0.3 + (comparisonCount * 0.05), 0.7);
        double statisticalConfidence = statisticallySignificant ? 0.8 : 0.5;

        return BigDecimal.valueOf((rulesConfidence + dataConfidence + statisticalConfidence) / 3);
    }

    private FraudAnalysis.FraudStatus determineStatus(int riskScore) {
        if (riskScore >= 80) return FraudAnalysis.FraudStatus.REJECTED;
        if (riskScore >= 60) return FraudAnalysis.FraudStatus.MANUAL_REVIEW;
        return FraudAnalysis.FraudStatus.APPROVED;
    }

    public void learnFromFeedback(UUID transactionId, boolean wasActualFraud) {
        Optional<FraudAnalysis> analysisOpt = fraudAnalysisRepository.findByTransactionId(transactionId);
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);

        if (analysisOpt.isPresent() && transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();

            Map<String, Object> ruleResults = ruleEngine.evaluateRules(transaction);
            int discreteRiskScore = (int) ruleResults.get("discreteRiskScore");
            Map<String, Object> similarityResults = similarityAnalyzer.analyzeSimilarity(transaction);
            double anomalyScore = (double) similarityResults.get("anomalyScore");

            double ruleScoreNormalized = discreteRiskScore / 100.0;

            Map<String, Object> optimizationResults = gradientOptimizer.optimizeWeights(
                    ruleScoreNormalized, anomalyScore, wasActualFraud);

            log.info("Learning from feedback - Transaction: {}, WasFraud: {}, ErrorReduction: {}",
                    transactionId, wasActualFraud, optimizationResults.get("errorReduction"));
        }
    }
}