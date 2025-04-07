package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many plans can be offered by a single ServiceProvider 
     * (e.g., Ooredoo, Mobilis, Sonelgaz).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    /**
     * Instead of storing a string planType (e.g., "PREPAID", "POSTPAID"), 
     * we reference an existing TransactionType. For example, "BILL_PAYMENT" or "P2P_TRANSFER".
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;

    /**
     * A user-friendly name for the plan/offer:
     * e.g., "Ooredoo Gold 10GB", "Sonelgaz Bill Payment", etc.
     */
    @Column(nullable = false)
    private String planName;

    /**
     * Price could represent:
     *  - The cost of a one-time top-up or bundle
     *  - A monthly fee if it's postpaid
     *  - Zero if the price is variable or determined elsewhere
     */
    private BigDecimal price;

    /**
     * Optional fields for telecom or other services:
     * e.g. data volume (in MB), call minutes, validityDays, etc.
     */
    private Integer dataVolume;  
    private Integer validityDays;
    private Integer callMinutes;

    @Column(length = 1000)
    private String description;

    /**
     * Basic timestamps.
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
