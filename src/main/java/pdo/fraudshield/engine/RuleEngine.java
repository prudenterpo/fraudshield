package pdo.fraudshield.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pdo.fraudshield.entity.Transaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RuleEngine {

    public Map<String, Object> evaluateRules(Transaction transaction) {
        Map<String, Object> results = new HashMap<>();

        boolean highAmount = evaluateHighAmountRule(transaction);
        results.put("highAmount", highAmount);
        results.put("highAmountThreshold", BigDecimal.valueOf(5000));

        boolean unusualTime = evaluateUnusualTimeRule(transaction);
        results.put("unusualTime", unusualTime);
        results.put("normalHours", "06:00-22:00");

        boolean newDevice = evaluateNewDeviceRule(transaction);
        results.put("newDevice", newDevice);

        boolean locationAnomaly = evaluateLocationAnomaly(transaction);
        results.put("locationAnomaly", locationAnomaly);

        int riskScore = calculateCombinatorialRisk(highAmount, unusualTime, newDevice, locationAnomaly);
        results.put("discreteRiskScore", riskScore);
        results.put("triggeredRules", countTriggeredRules(highAmount, unusualTime, newDevice, locationAnomaly));

        return results;
    }

    private boolean evaluateHighAmountRule(Transaction transaction) {
        return transaction.getAmount().compareTo(BigDecimal.valueOf(5000)) > 0;
    }

    private boolean evaluateUnusualTimeRule(Transaction transaction) {
        int hour = transaction.getTimestamp().getHour();
        return hour < 6 || hour > 22; // Night hours
    }

    private boolean evaluateNewDeviceRule(Transaction transaction) {
        // For now, simple check - will be enhanced with customer device history
        return transaction.getDeviceId() != null &&
                transaction.getDeviceId().startsWith("new-");
    }

    private boolean evaluateLocationAnomaly(Transaction transaction) {
        // Simple implementation - will be enhanced with customer location history
        return transaction.getLocation() != null &&
                transaction.getLocation().contains("HIGH_RISK_");
    }

    private int calculateCombinatorialRisk(boolean... rules) {
        int triggered = 0;
        for (boolean rule : rules) {
            if (rule) triggered++;
        }

        // Combinatorial risk calculation
        return switch (triggered) {
            case 0 -> 10;   // Low risk
            case 1 -> 40;   // Medium risk
            case 2 -> 70;   // High risk
            case 3 -> 90;   // Very high risk
            case 4 -> 95;   // Critical risk
            default -> 10;
        };
    }

    private int countTriggeredRules(boolean... rules) {
        int count = 0;
        for (boolean rule : rules) {
            if (rule) count++;
        }
        return count;
    }
}