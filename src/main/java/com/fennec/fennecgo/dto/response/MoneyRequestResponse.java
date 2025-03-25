package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class MoneyRequestResponse {
    private Long id;
    private BigDecimal amount;
    private String status;
    private String referenceNumber;
    private LocalDateTime createdAt;
    
    // Details shown to the payer about the requestor
    private String requestorUsername;
    private String requestorPhone;
    private String requestorEmail;
    
    // The note provided by the requestor
    private String note;
}
