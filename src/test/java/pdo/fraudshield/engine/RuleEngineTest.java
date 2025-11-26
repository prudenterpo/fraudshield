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
class RuleEngineTest {

    @Autowired
    private RuleEngine ruleEngine;

    @Test
    void shouldDetectHighAmountTransaction() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("6000.00"))
                .merchant("Test Merchant")
                .timestamp(LocalDateTime.now())
                .location("Test Location")
                .deviceId("trusted-device")
                .paymentMethod(Transaction.PaymentMethod.CREDIT_CARD)
                .build();

        Map<String, Object> results = ruleEngine.evaluateRules(transaction);

        assertTrue((Boolean) results.get("highAmount"));
        assertTrue((Integer) results.get("discreteRiskScore") > 50);
    }

    @Test
    void shouldDetectNightTimeTransaction() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .merchant("Test Merchant")
                .timestamp(LocalDateTime.of(2024, 1, 1, 3, 0)) // 3 AM
                .location("Test Location")
                .deviceId("trusted-device")
                .paymentMethod(Transaction.PaymentMethod.CREDIT_CARD)
                .build();

        Map<String, Object> results = ruleEngine.evaluateRules(transaction);

        assertTrue((Boolean) results.get("unusualTime"));
    }

    @Test
    void shouldCalculateCombinatorialRiskCorrectly() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("6000.00"))
                .merchant("Test Merchant")
                .timestamp(LocalDateTime.of(2024, 1, 1, 3, 0))
                .location("HIGH_RISK_AREA")
                .deviceId("new-device-123")
                .paymentMethod(Transaction.PaymentMethod.PIX)
                .build();

        Map<String, Object> results = ruleEngine.evaluateRules(transaction);

        int riskScore = (int) results.get("discreteRiskScore");
        int triggeredRules = (int) results.get("triggeredRules");

        assertTrue(riskScore >= 90);
        assertTrue(triggeredRules >= 3);
    }
}