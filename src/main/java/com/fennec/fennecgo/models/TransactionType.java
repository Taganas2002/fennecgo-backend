package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction_type")
public class TransactionType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;          // e.g., "P2P_TRANSFER", "BILL_PAY", "TOPUP"
    private String description;
}
