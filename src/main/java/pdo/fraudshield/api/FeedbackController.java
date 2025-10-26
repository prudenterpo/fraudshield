package pdo.fraudshield.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pdo.fraudshield.service.TransactionProcessor;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final TransactionProcessor transactionProcessor;

    @PostMapping("/fraud")
    public ResponseEntity<String> provideFraudFeedback(@RequestBody FraudFeedbackRequest request) {
        transactionProcessor.learnFromFeedback(request.transactionId(), request.wasFraud());
        return ResponseEntity.ok("Feedback received - model updated");
    }

    public record FraudFeedbackRequest(UUID transactionId, boolean wasFraud) {}
}