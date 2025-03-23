package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long transactionId;
    private BigDecimal amount;          // The requested amount
    private BigDecimal fee;             // The calculated fee
    private BigDecimal totalCharge;     // amount + fee (for deposits, this is what the user is charged)
    private BigDecimal newBalance;      // Expected new wallet balance after deposit OR remaining balance after withdrawal
    private String paymentMethod;       // e.g. "CIB_EDAHABIA", "BARIDI_MOB", "AGENT"
    private String status;              // e.g., PENDING, SUCCESS, FAILED, CANCELED
    private String referenceNumber;     // Unique transaction reference
    private LocalDateTime createdAt;
}
