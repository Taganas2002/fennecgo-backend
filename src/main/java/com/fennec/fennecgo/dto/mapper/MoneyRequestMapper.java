// File: src/main/java/com/fennec/fennecgo/dto/mapper/MoneyRequestMapper.java
package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import com.fennec.fennecgo.models.MoneyRequest;
import org.springframework.stereotype.Component;

@Component
public class MoneyRequestMapper {

    public MoneyRequestResponse toMoneyRequestResponse(MoneyRequest moneyRequest) {
        if (moneyRequest == null) return null;
        MoneyRequestResponse response = new MoneyRequestResponse();
        response.setId(moneyRequest.getId());
        response.setFromWalletID(moneyRequest.getFromWalletID() != null ? moneyRequest.getFromWalletID().getId() : null);
        response.setToWalletID(moneyRequest.getToWalletID() != null ? moneyRequest.getToWalletID().getId() : null);
        response.setAmount(moneyRequest.getAmount());
        response.setNote(moneyRequest.getNote());
        response.setStatus(moneyRequest.getStatus());
        response.setCreatedAt(moneyRequest.getCreatedAt());
        return response;
    }

    /**
     * Build a MoneyRequest entity using:
     * - payerWalletId: the ID of the payer's default wallet (from lookup)
     * - requestorWalletId: the ID of the requestor's default wallet (from authenticated user)
     */
    public MoneyRequest toMoneyRequest(MoneyRequestRequest request, 
                                       Long payerWalletId, 
                                       Long requestorWalletId) {
        MoneyRequest moneyRequest = new MoneyRequest();
        // Note: We'll set the Wallet associations later in the service (by looking up the Wallet entities)
        // For now, we assume that the service will use these IDs to fetch the Wallet entities.
        // Here, we just set the amount, note, and default status.
        moneyRequest.setAmount(request.getAmount());
        moneyRequest.setNote(request.getNote());
        moneyRequest.setStatus("PENDING");
        // The associations will be set in the service layer after fetching the entities:
        // moneyRequest.setFromWalletID(payerWallet);
        // moneyRequest.setToWalletID(requestorWallet);
        return moneyRequest;
    }
}
