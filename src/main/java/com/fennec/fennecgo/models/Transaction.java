package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ManyToOne relationships (lazy loading recommended for large data)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_type_id")
    private TransactionType transactionType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_wallet_id")
    private Wallet fromWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_wallet_id")
    private Wallet toWallet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "biller_id")
    private Biller biller;

    // Transaction fields
    private BigDecimal amount;
    
    private BigDecimal fee;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;          // e.g. "PENDING", "COMPLETED", "FAILED"
    
    private String referenceNumber; 
    
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now(); // Initialize updatedAt on creation as well
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    
}
