package com.fennec.fennecgo.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class MyActivityResponse {

    private List<TransactionResponse> all;
    private List<TransactionResponse> sent;
    private List<TransactionResponse> received;
    
}
