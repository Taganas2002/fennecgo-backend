package com.fennec.fennecgo.dto.response;

import lombok.Data;

@Data
public class BillerResponse {
    private Long id;
    private String externalCode;
    private String name;
    private String category;
    private String logoUrl;
    private String description;
}
