package com.fennec.fennecgo.dto.response;

import lombok.Data;

@Data
public class TransactionTypeResponse {
    private Long id;
    private String code;
    private String name;
    private String category;
    private String description;
}
