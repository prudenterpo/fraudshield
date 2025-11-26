package pdo.fraudshield;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pdo.fraudshield.entity.Transaction;
import pdo.fraudshield.dto.TransactionRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FraudShieldIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldProcessTransactionThroughFullStack() {
        TransactionRequest request = new TransactionRequest(
                UUID.randomUUID(),
                new BigDecimal("150.00"),
                "Local Store",
                LocalDateTime.now(),
                "São Paulo",
                "trusted-device-123",
                Transaction.PaymentMethod.CREDIT_CARD
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/transactions/analyze",
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("APPROVED") || response.getBody().contains("riskScore"));
    }
}