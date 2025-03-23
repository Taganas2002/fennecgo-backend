package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WithdrawalResponse {
    private Long transactionId;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal totalCharge;
    private BigDecimal newBalance;
    private String paymentMethod;
    private String status;
    private String referenceNumber;
    private LocalDateTime createdAt;
}
