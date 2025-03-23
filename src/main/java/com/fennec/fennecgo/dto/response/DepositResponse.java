package com.fennec.fennecgo.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DepositResponse {
    private Long transactionId;        // Transaction ID
    private BigDecimal amount;         // The deposit amount
    private BigDecimal fee;            // Fee
    private BigDecimal totalCharge;    // amount + fee
    private BigDecimal newBalance;     // The updated or expected wallet balance
    private String depositMethod;
    private String status;            // "SUCCESS", "CANCELED", or remain "PENDING"
    private String paymentMethod;      // e.g. "AGENT"
    private String referenceNumber;    // Unique reference for tracking
    private LocalDateTime createdAt;   // When the transaction was initially created
}
