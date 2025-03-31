package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferInitiationResponse {
    private Long transactionId;
    private String referenceNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalCharge;
    private String paymentMethod;  // e.g. "WALLET"
    private String status;         // "PENDING"
    private LocalDateTime createdAt;

    // Optional: If you want to show note/description or sender info
    private String description;
    private String senderUsername;
    private String senderEmail;
}
