package com.fennec.fennecgo.dto.request;

import lombok.Data;

@Data
public class TransactionTypeRequest {
    private String code;
    private String name;
    private String category;
    private String description;
}
