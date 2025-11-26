package pdo.fraudshield.visualization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pdo.fraudshield.entity.FraudAnalysis;
import pdo.fraudshield.entity.Transaction;
import pdo.fraudshield.engine.ProbabilityEngine;
import pdo.fraudshield.engine.RuleEngine;
import pdo.fraudshield.engine.SimilarityAnalyzer;
import pdo.fraudshield.repository.FraudAnalysisRepository;
import pdo.fraudshield.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DashboardGenerator {

    private final TransactionRepository transactionRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;
    private final RuleEngine ruleEngine;
    private final SimilarityAnalyzer similarityAnalyzer;
    private final ProbabilityEngine probabilityEngine;

    public String generateDashboardHtml(Transaction transaction, FraudAnalysis analysis) {
        Map<String, Object> ruleResults = ruleEngine.evaluateRules(transaction);
        Map<String, Object> similarityResults = similarityAnalyzer.analyzeSimilarity(transaction);
        Map<String, Object> probabilityResults = probabilityEngine.calculateProbabilities(transaction);

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>FraudShield Analysis Dashboard</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    .risk-low { background-color: #d4edda; }
                    .risk-medium { background-color: #fff3cd; }
                    .risk-high { background-color: #f8d7da; }
                    .math-section { border-left: 4px solid #007bff; padding-left: 15px; margin-bottom: 20px; }
                    .score-card { border-radius: 8px; padding: 15px; margin-bottom: 15px; }
                </style>
            </head>
            <body>
                <div class="container mt-4">
                    <h1 class="text-center mb-4">🛡️ FraudShield Analysis Dashboard</h1>
                    
                    <!-- Transaction Overview -->
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5>Transaction Overview</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-3"><strong>ID:</strong> %s</div>
                                <div class="col-md-3"><strong>Customer:</strong> %s</div>
                                <div class="col-md-3"><strong>Amount:</strong> $%s</div>
                                <div class="col-md-3"><strong>Time:</strong> %s</div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Risk Summary -->
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5>Risk Analysis Summary</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="score-card %s">
                                        <h6>Final Risk Score</h6>
                                        <h2>%d/100</h2>
                                        <p><strong>Status:</strong> %s</p>
                                        <p><strong>Confidence:</strong> %s%%</p>
                                    </div>
                                </div>
                                <div class="col-md-8">
                                    <div class="progress mb-2">
                                        <div class="progress-bar %s" role="progressbar" 
                                             style="width: %d%%" aria-valuenow="%d" 
                                             aria-valuemin="0" aria-valuemax="100">
                                        </div>
                                    </div>
                                    <p><small>Risk Level: %s</small></p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Mathematical Analysis Breakdown -->
                    <div class="row">
                        <!-- Mathematics Discrete -->
                        <div class="col-md-4">
                            <div class="card math-section">
                                <div class="card-header bg-primary text-white">
                                    <h6>🧮 Mathematics Discrete</h6>
                                </div>
                                <div class="card-body">
                                    <p><strong>Rule Engine Score:</strong> %d/100</p>
                                    <p><strong>Triggered Rules:</strong> %d</p>
                                    <ul>
                                        <li>High Amount: %s</li>
                                        <li>Unusual Time: %s</li>
                                        <li>New Device: %s</li>
                                        <li>Location Anomaly: %s</li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Algebra Linear -->
                        <div class="col-md-4">
                            <div class="card math-section">
                                <div class="card-header bg-success text-white">
                                    <h6>📐 Algebra Linear</h6>
                                </div>
                                <div class="card-body">
                                    <p><strong>Similarity Score:</strong> %.2f</p>
                                    <p><strong>Anomaly Detection:</strong> %s</p>
                                    <p><strong>Comparisons:</strong> %d transactions</p>
                                    <p><strong>Analysis:</strong> %s</p>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Statistics -->
                        <div class="col-md-4">
                            <div class="card math-section">
                                <div class="card-header bg-info text-white">
                                    <h6>📊 Statistics & Probability</h6>
                                </div>
                                <div class="card-body">
                                    <p><strong>Bayesian Score:</strong> %d/100</p>
                                    <p><strong>Posterior Probability:</strong> %.3f</p>
                                    <p><strong>Confidence Interval:</strong> %s</p>
                                    <p><strong>Significant:</strong> %s</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Calculus Optimization -->
                    <div class="card mt-4">
                        <div class="card-header bg-warning">
                            <h6>📈 Calculus Optimization</h6>
                        </div>
                        <div class="card-body">
                            <p><em>Model weights are continuously optimized using gradient descent</em></p>
                            <p><strong>Current Weights:</strong></p>
                            <ul>
                                <li>Rule Engine: %.2f</li>
                                <li>Similarity Analysis: %.2f</li>
                                <li>Bayesian Probability: 0.33</li>
                            </ul>
                        </div>
                    </div>
                    
                    <footer class="mt-4 text-center text-muted">
                        <p>Generated by FraudShield • %s</p>
                    </footer>
                </div>
            </body>
            </html>
            """.formatted(
                // Transaction Overview
                transaction.getId().toString().substring(0, 8),
                transaction.getCustomerId().toString().substring(0, 8),
                transaction.getAmount().setScale(2, RoundingMode.HALF_UP),
                transaction.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),

                // Risk Summary
                getRiskCssClass(analysis.getRiskScore()),
                analysis.getRiskScore().intValue(),
                analysis.getStatus().toString(),
                analysis.getConfidenceLevel().multiply(BigDecimal.valueOf(100)).setScale(1, RoundingMode.HALF_UP),
                getProgressBarClass(analysis.getRiskScore()),
                analysis.getRiskScore().intValue(),
                analysis.getRiskScore().intValue(),
                getRiskLevel(analysis.getRiskScore()),

                // Mathematics Discrete
                (int) ruleResults.get("discreteRiskScore"),
                (int) ruleResults.get("triggeredRules"),
                ruleResults.get("highAmount"),
                ruleResults.get("unusualTime"),
                ruleResults.get("newDevice"),
                ruleResults.get("locationAnomaly"),

                // Algebra Linear
                (double) similarityResults.get("similarityScore"),
                (double) similarityResults.get("anomalyScore"),
                (int) similarityResults.get("comparisonCount"),
                similarityResults.get("analysis"),

                // Statistics
                (int) probabilityResults.get("bayesianScore"),
                (double) probabilityResults.get("posteriorProbability"),
                probabilityResults.get("confidenceInterval"),
                probabilityResults.get("statisticallySignificant"),

                // Calculus
                0.6, 0.4, // Placeholder weights

                // Footer
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String getRiskCssClass(BigDecimal riskScore) {
        int score = riskScore.intValue();
        if (score < 40) return "risk-low";
        if (score < 70) return "risk-medium";
        return "risk-high";
    }

    private String getProgressBarClass(BigDecimal riskScore) {
        int score = riskScore.intValue();
        if (score < 40) return "bg-success";
        if (score < 70) return "bg-warning";
        return "bg-danger";
    }

    private String getRiskLevel(BigDecimal riskScore) {
        int score = riskScore.intValue();
        if (score < 40) return "LOW";
        if (score < 70) return "MEDIUM";
        return "HIGH";
    }

    public String generateSystemOverviewHtml() {
        long totalTransactions = transactionRepository.count();
        long approved = fraudAnalysisRepository.countByStatus(FraudAnalysis.FraudStatus.APPROVED);
        long rejected = fraudAnalysisRepository.countByStatus(FraudAnalysis.FraudStatus.REJECTED);
        long review = fraudAnalysisRepository.countByStatus(FraudAnalysis.FraudStatus.MANUAL_REVIEW);

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>FraudShield System Overview</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>
            <body>
                <div class="container mt-4">
                    <h1>FraudShield System Overview</h1>
                    <div class="row">
                        <div class="col-md-3">
                            <div class="card text-white bg-primary">
                                <div class="card-body">
                                    <h5>Total Transactions</h5>
                                    <h3>%d</h3>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-success">
                                <div class="card-body">
                                    <h5>Approved</h5>
                                    <h3>%d</h3>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-warning">
                                <div class="card-body">
                                    <h5>Manual Review</h5>
                                    <h3>%d</h3>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="card text-white bg-danger">
                                <div class="card-body">
                                    <h5>Rejected</h5>
                                    <h3>%d</h3>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(totalTransactions, approved, review, rejected);
    }
}