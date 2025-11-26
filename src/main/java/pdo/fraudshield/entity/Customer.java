package pdo.fraudshield.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String document;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_profile")
    private CustomerRiskProfile riskProfile;

    // Computed fields - not persisted
    @Transient
    private BigDecimal averageTransactionAmount;

    @Transient
    private List<String> commonLocations;

    public enum CustomerRiskProfile {
        LOW, MEDIUM, HIGH, NEW_CUSTOMER
    }
}