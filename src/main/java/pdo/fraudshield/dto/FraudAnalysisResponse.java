package pdo.fraudshield.dto;

import java.math.BigDecimal;

public record FraudAnalysisResponse(
        boolean fraud,
        BigDecimal riskScore,
        String reason
) {}
