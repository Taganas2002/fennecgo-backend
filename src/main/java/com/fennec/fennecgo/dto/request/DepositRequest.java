package com.fennec.fennecgo.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DepositRequest {
    private Long walletId;         // The wallet to top up
    private BigDecimal amount;     // The deposit amount
    private Long paymentMethodId;  // Payment method ID
    // Removed 'description'
}
