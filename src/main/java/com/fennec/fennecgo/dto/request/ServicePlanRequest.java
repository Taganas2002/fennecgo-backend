
package com.fennec.fennecgo.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServicePlanRequest {
    private Long serviceProviderId;
    private Long transactionTypeId;
    private String planName;
    private BigDecimal price;
    private Integer validityDays;
    private Integer dataVolume;
    private Integer callMinutes;
    private String description;
}
