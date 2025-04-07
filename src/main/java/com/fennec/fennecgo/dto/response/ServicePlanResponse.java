package com.fennec.fennecgo.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServicePlanResponse {

    private Long id;

    private Long serviceProviderId;   // The provider's ID
    private Long transactionTypeId;   // The transaction type ID

    private String planName;
    private BigDecimal price;
    private Integer validityDays;
    private Integer dataVolume;
    private Integer callMinutes;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
