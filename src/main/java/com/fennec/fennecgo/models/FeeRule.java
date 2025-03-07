package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fee_rule")
public class FeeRule {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // FeeRule is associated with a TransactionType
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;
    
    @Column(name = "min_amount")
    private BigDecimal minAmount;
    
    @Column(name = "max_amount")
    private BigDecimal maxAmount;
    
    @Column(name = "fixed_fee")
    private BigDecimal fixedFee;
    
    @Column(name = "percentage_fee")
    private BigDecimal percentageFee; // e.g., 0.015 for 1.5%
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "is_active")
    private Boolean isActive;
}
