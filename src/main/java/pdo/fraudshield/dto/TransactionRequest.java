package pdo.fraudshield.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import pdo.fraudshield.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRequest(
        @NotNull UUID customerId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String merchant,
        @NotNull LocalDateTime timestamp,
        @NotBlank String location,
        @NotBlank String deviceId,
        @NotNull Transaction.PaymentMethod paymentMethod
) {}
