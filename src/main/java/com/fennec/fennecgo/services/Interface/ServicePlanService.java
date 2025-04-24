package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.ServicePlanRequest;
import com.fennec.fennecgo.dto.response.ServicePlanResponse;
import com.fennec.fennecgo.dto.response.TransactionTypeResponse;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicePlanService {
    ServicePlanResponse createServicePlan(ServicePlanRequest request);
    ServicePlanResponse getServicePlanById(Long id);
    ServicePlanResponse updateServicePlan(Long id, ServicePlanRequest request);
    void deleteServicePlan(Long id);
    Page<ServicePlanResponse> listServicePlans(Long providerId, Long transactionTypeId, Pageable pageable);
    List<TransactionTypeResponse> listTransactionTypesByProvider(Long providerId);

}

