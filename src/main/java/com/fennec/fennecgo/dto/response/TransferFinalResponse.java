package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferFinalResponse {
    private Long transactionId;
    private String referenceNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalCharge;
    private String paymentMethod; // "WALLET"
    private String status;        // "SUCCESS"
    private LocalDateTime createdAt;

    // Updated balances, if you want to show them:
    private BigDecimal senderNewBalance;
    private BigDecimal receiverNewBalance;

    // Optional: note/description
    private String description;
}
