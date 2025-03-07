package com.fennec.fennecgo.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MoneyRequestRequest {
    // The payer's user ID (recipient is searched by user in the front end)
    private Long payerUserId;
    private BigDecimal amount;
    private String note;
}
