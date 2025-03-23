package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction_type")
public class TransactionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A unique code for this transaction type.
     * Examples: "BILL_PAYMENT", "P2P_TRANSFER", "MERCHANT_PAYMENT", "TOPUP"
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * A human-readable name.
     * Examples: "Bill Payment", "Peer-to-Peer Transfer", "Merchant Payment"
     */
    @Column(nullable = false)
    private String name;

    /**
     * A category to group transaction types.
     * Examples: "BILLING", "TRANSFER", "MERCHANT", "TOPUP"
     */
    private String category;

    /**
     * An optional description or notes about this transaction type.
     */
    @Column(length = 1000)
    private String description;
}
