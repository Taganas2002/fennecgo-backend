package com.fennec.fennecgo.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FeeRuleResponse {
    private Long id;
    private Long transactionTypeId;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal fixedFee;
    private BigDecimal percentageFee;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
