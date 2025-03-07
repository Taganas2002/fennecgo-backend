package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "money_request")
public class MoneyRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // The wallet that is requesting money
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_wallet_id", nullable = false)
    private Wallet fromWalletID;
    
    // The wallet expected to pay
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_wallet_id", nullable = false)
    private Wallet toWalletID;
    
    private BigDecimal amount;
    private String note;
    private String status;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
