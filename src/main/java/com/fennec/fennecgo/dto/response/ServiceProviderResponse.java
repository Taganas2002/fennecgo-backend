package com.fennec.fennecgo.dto.response;

import com.fennec.fennecgo.models.ServiceCategory;

import lombok.Data;

@Data
public class ServiceProviderResponse {
    private Long id;
    private String externalCode;
    private String name;
    private String category;
    private String logoUrl;
    private String description;
}
