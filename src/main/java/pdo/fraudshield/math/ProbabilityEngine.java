package pdo.fraudshield.math;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pdo.fraudshield.domain.FraudAnalysis;
import pdo.fraudshield.domain.Transaction;
import pdo.fraudshield.repository.FraudAnalysisRepository;
import pdo.fraudshield.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProbabilityEngine {

    private final TransactionRepository transactionRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;

    // Prior probabilities (would normally come from historical data)
    private static final double PRIOR_PROBABILITY_FRAUD = 0.02; // 2% of transactions are fraudulent
    private static final double PRIOR_PROBABILITY_LEGITIMATE = 0.98; // 98% are legitimate

    public Map<String, Object> calculateProbabilities(Transaction transaction) {
        Map<String, Object> results = new HashMap<>();

        // Calculate likelihoods for each feature
        double likelihoodAmount = calculateAmountLikelihood(transaction.getAmount());
        double likelihoodTime = calculateTimeLikelihood(transaction.getTimestamp().getHour());
        double likelihoodDevice = calculateDeviceLikelihood(transaction.getDeviceId());
        double likelihoodLocation = calculateLocationLikelihood(transaction.getLocation());

        // Bayesian inference: P(Fraud | Evidence) = P(Evidence | Fraud) * P(Fraud) / P(Evidence)
        double numerator = likelihoodAmount * likelihoodTime * likelihoodDevice * likelihoodLocation * PRIOR_PROBABILITY_FRAUD;

        // Calculate P(Evidence) using law of total probability
        double evidenceLegitimate = calculateEvidenceLegitimate(transaction);
        double denominator = numerator + evidenceLegitimate;

        double posteriorProbability = denominator > 0 ? numerator / denominator : 0.0;

        results.put("posteriorProbability", round(posteriorProbability, 4));
        results.put("priorProbability", PRIOR_PROBABILITY_FRAUD);
        results.put("likelihoodAmount", likelihoodAmount);
        results.put("likelihoodTime", likelihoodTime);
        results.put("likelihoodDevice", likelihoodDevice);
        results.put("likelihoodLocation", likelihoodLocation);
        results.put("bayesianScore", (int) (posteriorProbability * 100));

        // Statistical significance test
        results.put("statisticallySignificant", isStatisticallySignificant(posteriorProbability));
        results.put("confidenceInterval", calculateConfidenceInterval(posteriorProbability));

        return results;
    }

    private double calculateAmountLikelihood(BigDecimal amount) {
        double amountValue = amount.doubleValue();

        // Exponential distribution: higher amounts more likely to be fraud
        if (amountValue > 10000) return 0.8;
        if (amountValue > 5000) return 0.6;
        if (amountValue > 1000) return 0.3;
        return 0.1;
    }

    private double calculateTimeLikelihood(int hour) {
        // Fraud more likely at night
        if (hour < 6 || hour > 22) return 0.7;  // Night hours
        return 0.2;  // Day hours
    }

    private double calculateDeviceLikelihood(String deviceId) {
        if (deviceId == null) return 0.5;
        if (deviceId.startsWith("new-")) return 0.8;
        if (deviceId.startsWith("trusted-")) return 0.1;
        return 0.3;
    }

    private double calculateLocationLikelihood(String location) {
        if (location == null) return 0.5;
        if (location.contains("HIGH_RISK_")) return 0.9;
        if (location.contains("LOW_RISK_")) return 0.1;
        return 0.4;
    }

    private double calculateEvidenceLegitimate(Transaction transaction) {
        // P(Evidence | Legitimate) * P(Legitimate)
        double likelihoodAmount = 1 - calculateAmountLikelihood(transaction.getAmount());
        double likelihoodTime = 1 - calculateTimeLikelihood(transaction.getTimestamp().getHour());
        double likelihoodDevice = 1 - calculateDeviceLikelihood(transaction.getDeviceId());
        double likelihoodLocation = 1 - calculateLocationLikelihood(transaction.getLocation());

        return likelihoodAmount * likelihoodTime * likelihoodDevice * likelihoodLocation * PRIOR_PROBABILITY_LEGITIMATE;
    }

    private boolean isStatisticallySignificant(double probability) {
        // Simple significance test: probability significantly different from prior
        return Math.abs(probability - PRIOR_PROBABILITY_FRAUD) > 0.1;
    }

    private String calculateConfidenceInterval(double probability) {
        // Simplified confidence interval calculation
        double marginOfError = 1.96 * Math.sqrt(probability * (1 - probability) / 1000); // Assuming 1000 samples
        double lower = Math.max(0, probability - marginOfError);
        double upper = Math.min(1, probability + marginOfError);

        return String.format("[%.3f, %.3f]", lower, upper);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Method to update priors based on actual data (would be called periodically)
    public void updatePriorProbabilities() {
        long totalTransactions = transactionRepository.count();
        long fraudulentTransactions = fraudAnalysisRepository.countByStatus(FraudAnalysis.FraudStatus.REJECTED);

        if (totalTransactions > 100) { // Only update with sufficient data
            double newPrior = (double) fraudulentTransactions / totalTransactions;
            // In a real implementation, we'd use instance variables instead of static
            // For now, we'll log the update but not change the static finals
            log.info("Update this message: {}", newPrior);
        }
    }
}