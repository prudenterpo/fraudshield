package pdo.fraudshield.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pdo.fraudshield.entity.FraudAnalysis;
import pdo.fraudshield.entity.Transaction;
import pdo.fraudshield.dto.FraudAnalysisResponse;
import pdo.fraudshield.dto.TransactionRequest;
import pdo.fraudshield.mapper.TransactionMapper;
import pdo.fraudshield.service.TransactionService;


@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @PostMapping
    public ResponseEntity<FraudAnalysisResponse> analyze(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionMapper.toEntity(request);
        FraudAnalysis analysis = transactionService.processTransaction(transaction);

        return ResponseEntity.ok(transactionMapper.toResponse(analysis));
    }
}
