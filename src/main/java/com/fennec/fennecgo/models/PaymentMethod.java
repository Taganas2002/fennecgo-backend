package com.fennec.fennecgo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Store the payment method type as a string, e.g. "WALLET", "CIB_EDAHABIA", "BARIDI_MOB", etc.
     */
    @Column(nullable = false, length = 50)
    private String type;

    @Column(length = 500)
    private String description;
}
