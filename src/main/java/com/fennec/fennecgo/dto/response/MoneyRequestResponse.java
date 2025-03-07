package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MoneyRequestResponse {
    private Long id;
    private Long fromWalletID;  // payer's wallet
    private Long toWalletID;    // requestor's wallet
    private BigDecimal amount;
    private String note;
    private String status;
    private Date createdAt;
}