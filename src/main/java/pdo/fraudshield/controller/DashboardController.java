package pdo.fraudshield.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pdo.fraudshield.entity.FraudAnalysis;
import pdo.fraudshield.entity.Transaction;
import pdo.fraudshield.repository.FraudAnalysisRepository;
import pdo.fraudshield.repository.TransactionRepository;
import pdo.fraudshield.visualization.DashboardGenerator;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardGenerator dashboardGenerator;
    private final TransactionRepository transactionRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<String> getTransactionDashboard(@PathVariable UUID transactionId) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        Optional<FraudAnalysis> analysisOpt = fraudAnalysisRepository.findByTransactionId(transactionId);

        if (transactionOpt.isPresent() && analysisOpt.isPresent()) {
            String html = dashboardGenerator.generateDashboardHtml(transactionOpt.get(), analysisOpt.get());
            return ResponseEntity.ok(html);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/overview")
    public ResponseEntity<String> getSystemOverview() {
        String html = dashboardGenerator.generateSystemOverviewHtml();
        return ResponseEntity.ok(html);
    }
}