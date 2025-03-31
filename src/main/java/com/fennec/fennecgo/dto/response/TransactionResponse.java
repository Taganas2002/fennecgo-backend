package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fennec.fennecgo.models.ServiceProvider;

@Data
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;          // The requested amount
    private String direction;
    private BigDecimal fee;             // The calculated fee
    private BigDecimal totalCharge;     // amount + fee (for deposits, this is what the user is charged)
    private String description;      // Expected new wallet balance after deposit OR remaining balance after withdrawal
    private String paymentMethod;       // e.g. "CIB_EDAHABIA", "BARIDI_MOB", "AGENT"
    private String status;              // e.g., PENDING, SUCCESS, FAILED, CANCELED
    private String referenceNumber;     // Unique transaction reference
    private String fromUserName;
    private String toUserName;
    private LocalDateTime createdAt;
    private String transactionType;    // e.g., DEPOSIT, WITHDRAWAL, etc.
    private String serviceProvider;
}
