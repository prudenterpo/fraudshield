package pdo.fraudshield.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pdo.fraudshield.domain.FraudAnalysis;
import pdo.fraudshield.domain.Transaction;
import pdo.fraudshield.service.TransactionProcessor;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionProcessor transactionProcessor;

    @PostMapping("/analyze")
    public ResponseEntity<FraudAnalysis> analyzeTransaction(@RequestBody TransactionRequest request) {
        Transaction transaction = mapToTransaction(request);
        FraudAnalysis analysis = transactionProcessor.processTransaction(transaction);
        return ResponseEntity.ok(analysis);
    }

    private Transaction mapToTransaction(TransactionRequest request) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .customerId(request.customerId())
                .amount(request.amount())
                .merchant(request.merchant())
                .timestamp(request.timestamp())
                .location(request.location())
                .deviceId(request.deviceId())
                .paymentMethod(request.paymentMethod())
                .build();
    }

    public record TransactionRequest(
            UUID customerId,
            java.math.BigDecimal amount,
            String merchant,
            java.time.LocalDateTime timestamp,
            String location,
            String deviceId,
            Transaction.PaymentMethod paymentMethod
    ) {}
}