package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import com.fennec.fennecgo.models.MoneyRequest;
import com.fennec.fennecgo.models.Wallet;
import org.springframework.stereotype.Component;

@Component
public class MoneyRequestMapper {

    /**
     * Convert MoneyRequestRequest to MoneyRequest entity.
     * Note: The associations for payer and requestor wallets will be set in the service.
     */
    public MoneyRequest toMoneyRequest(MoneyRequestRequest request) {
        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setAmount(request.getAmount());
        // Generate a basic reference number (the service will override with UUID)
        moneyRequest.setReferenceNumber("MR_" + System.currentTimeMillis());
        // Store the note in the description field.
        moneyRequest.setNote(request.getNote());
        return moneyRequest;
    }
    
    /**
     * Convert MoneyRequest entity to MoneyRequestResponse DTO.
     * The requestor's details are extracted from the toWallet.
     */
    public MoneyRequestResponse toMoneyRequestResponse(MoneyRequest moneyRequest) {
        MoneyRequestResponse response = new MoneyRequestResponse();
        response.setId(moneyRequest.getId());
        response.setAmount(moneyRequest.getAmount());
        response.setStatus(moneyRequest.getStatus());
        response.setReferenceNumber(moneyRequest.getReferenceNumber());
        response.setCreatedAt(moneyRequest.getCreatedAt());
        // Set the note from the description field
        response.setNote(moneyRequest.getNote());
        // Get requestor details from the toWallet
        Wallet requestorWallet = moneyRequest.getToWalletID();
        if(requestorWallet != null && requestorWallet.getUser() != null) {
            response.setRequestorUsername(requestorWallet.getUser().getUsername());
            response.setRequestorPhone(requestorWallet.getUser().getPhone());
            response.setRequestorEmail(requestorWallet.getUser().getEmail());
        }
        return response;
    }
}
