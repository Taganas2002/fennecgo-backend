package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletTransferResponse {
    private Long transactionId;
    private String referenceNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalCharge;
    private String status;            // e.g. "SUCCESS"
    private String paymentMethod;     // "WALLET"
    private LocalDateTime createdAt;
    // Sender information (from the current user)
    private String senderUsername;
    private String senderPhone;
    private String senderEmail;
    
    // The note or description provided with the transfer
    private String description;
}
