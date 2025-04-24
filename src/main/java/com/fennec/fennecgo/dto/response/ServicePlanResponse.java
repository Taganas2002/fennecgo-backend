// src/main/java/com/fennec/fennecgo/dto/response/ServicePlanResponse.java
package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServicePlanResponse {
    private Long id;
    private Long serviceProviderId;
    private Long transactionTypeId;
    private String planName;
    private BigDecimal price;
    private Integer validityDays;
    private Integer dataVolume;
    private Integer callMinutes;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
