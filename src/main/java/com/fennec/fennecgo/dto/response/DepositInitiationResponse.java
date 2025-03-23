package com.fennec.fennecgo.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DepositInitiationResponse {
    private Long transactionId;
    private String referenceNumber;
    private BigDecimal amount;
    private BigDecimal fee;
    private String paymentMethod;
    private String status;
    private String qrCodeBase64; // or a URL if you prefer
}

