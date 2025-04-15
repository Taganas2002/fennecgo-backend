package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import java.util.List;

public interface MoneyRequestService {
    MoneyRequestResponse createMoneyRequest(MoneyRequestRequest request);
    MoneyRequestResponse getMoneyRequestById(Long id);
    List<MoneyRequestResponse> getRequestsForUser();
    List<MoneyRequestResponse> getRequestsForPayer();
	MoneyRequestResponse cancelMoneyRequest(String referenceNumber);
	MoneyRequestResponse confirmMoneyRequest(String referenceNumber);
	MoneyRequestResponse declineMoneyRequest(String referenceNumber);
}
