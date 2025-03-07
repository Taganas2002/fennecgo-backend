package com.fennec.fennecgo.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WalletRequest {
    private Long userId;
    private String name;
    private String iban;
    private BigDecimal initialBalance;
    private String currency;
    private String walletType;
    private BigDecimal dailyLimit;
}
