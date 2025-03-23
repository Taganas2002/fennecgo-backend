package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WithdrawalInitiationResponse {
    private Long transactionId;
    private String referenceNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private String paymentMethod;
    private String status;
    private String qrCodeBase64;
    private LocalDateTime createdAt;
}
