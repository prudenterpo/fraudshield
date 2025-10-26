package pdo.fraudshield.math;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pdo.fraudshield.domain.FraudAnalysis;
import pdo.fraudshield.domain.Transaction;
import pdo.fraudshield.service.TransactionProcessor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionProcessorTest {

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Test
    void shouldProcessAndApproveNormalTransaction() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("100.00"))
                .merchant("Local Store")
                .timestamp(LocalDateTime.now())
                .location("Safe Location")
                .deviceId("trusted-device")
                .paymentMethod(Transaction.PaymentMethod.CREDIT_CARD)
                .build();

        FraudAnalysis analysis = transactionProcessor.processTransaction(transaction);

        assertNotNull(analysis);
        assertEquals(FraudAnalysis.FraudStatus.APPROVED, analysis.getStatus());
        assertTrue(analysis.getRiskScore().compareTo(new BigDecimal("50")) < 0);
    }

    @Test
    void shouldFlagSuspiciousTransactionForReview() {
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .amount(new BigDecimal("6000.00"))
                .merchant("Unknown Merchant")
                .timestamp(LocalDateTime.of(2024, 1, 1, 3, 0))
                .location("HIGH_RISK_AREA")
                .deviceId("new-device-123")
                .paymentMethod(Transaction.PaymentMethod.PIX)
                .build();

        FraudAnalysis analysis = transactionProcessor.processTransaction(transaction);

        assertNotNull(analysis);
        assertEquals(FraudAnalysis.FraudStatus.MANUAL_REVIEW, analysis.getStatus());
        assertTrue(analysis.getRiskScore().compareTo(new BigDecimal("60")) >= 0);
    }
}