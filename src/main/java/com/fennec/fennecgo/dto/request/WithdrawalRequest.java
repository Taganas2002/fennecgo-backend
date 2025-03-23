package com.fennec.fennecgo.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawalRequest {
    private Long walletId;         // The wallet to withdraw from
    private BigDecimal amount;     // The withdrawal amount
    private Long paymentMethodId;  // Payment method ID
    // Removed 'description'
}
