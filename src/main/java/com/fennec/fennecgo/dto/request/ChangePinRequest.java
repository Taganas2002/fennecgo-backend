package com.fennec.fennecgo.dto.request;

import lombok.Data;

@Data
public class ChangePinRequest {
    private String currentPin;
    private String newPin;

   
}
