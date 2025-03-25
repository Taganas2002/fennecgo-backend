package com.fennec.fennecgo.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WalletTransferRequest {
    private Long toUserId;      // Receiver's user ID
    private BigDecimal amount;  // Amount to transfer
    private String description; // Note or reason for sending money
}
