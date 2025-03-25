package com.fennec.fennecgo.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MoneyRequestRequest {
    private Long payerUserId;    // The ID of the user from whom money is requested
    private BigDecimal amount;   // The requested amount
    private String note;         // A note or reason for the money request
}
