package com.fennec.fennecgo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private Long transactionTypeId;
    private Long paymentMethodId;
    private Long fromWalletId;
    private Long toWalletId;
    private Long billerId;        // optional, if paying a bill
    private BigDecimal amount;
    private String description;
}
