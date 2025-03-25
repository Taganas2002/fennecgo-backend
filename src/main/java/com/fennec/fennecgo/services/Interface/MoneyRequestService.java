package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import java.util.List;

public interface MoneyRequestService {
    MoneyRequestResponse createMoneyRequest(MoneyRequestRequest request);
    MoneyRequestResponse getMoneyRequestById(Long id);
    List<MoneyRequestResponse> getRequestsForUser();
    List<MoneyRequestResponse> getRequestsForPayer();
    void cancelMoneyRequest(Long requestId);
    
    // Payer actions:
    MoneyRequestResponse confirmMoneyRequest(Long requestId);
    MoneyRequestResponse declineMoneyRequest(Long requestId);
}
