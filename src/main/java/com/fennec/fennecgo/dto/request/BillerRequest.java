package com.fennec.fennecgo.dto.request;

import lombok.Data;

@Data
public class BillerRequest {
    private String externalCode;
    private String name;
    private String category;
    private String logoUrl;
    private String description;
}
