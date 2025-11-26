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
class SimilarityAnalyzerTest {

    @Autowired
    private SimilarityAnalyzer similarityAnalyzer;

    @Test
    void shouldAnalyzeSimilarityForNewCustomer() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .merchant("Test Merchant")
                .timestamp(LocalDateTime.now())
                .location("Test Location")
                .deviceId("new-device")
                .paymentMethod(Transaction.PaymentMethod.CREDIT_CARD)
                .build();

        Map<String, Object> results = similarityAnalyzer.analyzeSimilarity(transaction);

        assertEquals("INSUFFICIENT_HISTORY", results.get("analysis"));
        assertEquals(0.0, results.get("similarityScore"));
    }

    @Test
    void shouldCalculateAnomalyScore() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("5000.00"))
                .merchant("Test Merchant")
                .timestamp(LocalDateTime.of(2024, 1, 1, 14, 0))
                .location("SAFE_LOCATION")
                .deviceId("trusted-device")
                .paymentMethod(Transaction.PaymentMethod.CREDIT_CARD)
                .build();

        Map<String, Object> results = similarityAnalyzer.analyzeSimilarity(transaction);

        assertNotNull(results.get("anomalyScore"), "Anomaly score should not be null");
        double anomalyScore = (double) results.get("anomalyScore");
        assertTrue(anomalyScore >= 0.0 && anomalyScore <= 1.0);
    }
}