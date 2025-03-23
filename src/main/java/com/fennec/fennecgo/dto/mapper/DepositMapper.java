package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.response.DepositResponse;
import com.fennec.fennecgo.models.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositMapper {

    public DepositResponse toResponse(Transaction transaction, String depositMethod, BigDecimal newBalance) {
        DepositResponse response = new DepositResponse();
        response.setTransactionId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setNewBalance(newBalance);
        response.setDepositMethod(depositMethod);
        response.setStatus(transaction.getStatus().name());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}
