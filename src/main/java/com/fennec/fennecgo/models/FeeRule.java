package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "fee_rules")
public class FeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal fixedFee;
    private BigDecimal percentageFee;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Set default value in Java code
    @Column(name = "active", nullable = false)
    private boolean active = false;
}
