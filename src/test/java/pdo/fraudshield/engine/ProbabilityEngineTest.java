package pdo.fraudshield.engine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pdo.fraudshield.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProbabilityEngineTest {

    @Autowired
    private ProbabilityEngine probabilityEngine;

    @Test
    void shouldCalculateHighProbabilityForSuspiciousTransaction() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("8000.00"))
                .merchant("Test Merchant")
                .timestamp(LocalDateTime.of(2024, 1, 1, 3, 0))
                .location("HIGH_RISK_AREA")
                .deviceId("new-device-123")
                .paymentMethod(Transaction.PaymentMethod.PIX)
                .build();

        Map<String, Object> results = probabilityEngine.calculateProbabilities(transaction);

        double posteriorProbability = (double) results.get("posteriorProbability");
        int bayesianScore = (int) results.get("bayesianScore");

        assertTrue(posteriorProbability > 0.5);
        assertTrue(bayesianScore > 50);
    }

    @Test
    void shouldCalculateLowProbabilityForNormalTransaction() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .merchant("Local Store")
                .timestamp(LocalDateTime.of(2024, 1, 1, 14, 0))
                .location("LOW_RISK_AREA")
                .deviceId("trusted-device")
                .paymentMethod(Transaction.PaymentMethod.CREDIT_CARD)
                .build();

        Map<String, Object> results = probabilityEngine.calculateProbabilities(transaction);

        double posteriorProbability = (double) results.get("posteriorProbability");
        int bayesianScore = (int) results.get("bayesianScore");

        assertTrue(posteriorProbability < 0.3);
        assertTrue(bayesianScore < 30);
    }
}