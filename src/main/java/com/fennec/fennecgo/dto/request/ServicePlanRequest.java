package com.fennec.fennecgo.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicePlanRequest {

    private Long serviceProviderId;   // e.g. 1 for Ooredoo
    private Long transactionTypeId;   // e.g. 2 for "BILL_PAYMENT"

    private String planName;          // e.g. "Ooredoo Gold 10GB"
    private BigDecimal price;         // e.g. 1000.00
    private Integer validityDays;     // e.g. 30
    private Integer dataVolume;       // e.g. 10000 MB
    private Integer callMinutes;      // e.g. 300
    private String description;       // e.g. "Prepaid plan for 30 days"
}
