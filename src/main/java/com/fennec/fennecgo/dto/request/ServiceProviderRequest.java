package com.fennec.fennecgo.dto.request;

import lombok.Data;

@Data
public class ServiceProviderRequest {
    private String externalCode;
    private String name;
    private Long category;
    private String logoUrl;
    private String description;
}
