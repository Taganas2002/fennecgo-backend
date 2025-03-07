package com.fennec.fennecgo.services.Interface;


import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import java.util.List;

public interface MoneyRequestService {
    MoneyRequestResponse createMoneyRequest(Long requestorUserId, MoneyRequestRequest request);
    MoneyRequestResponse getMoneyRequestById(Long id);
    List<MoneyRequestResponse> getRequestsForUser(Long userId);
    List<MoneyRequestResponse> getRequestsForPayer(Long userId);
    void cancelMoneyRequest(Long requestId);
}
