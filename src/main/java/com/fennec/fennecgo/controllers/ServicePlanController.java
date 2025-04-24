package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.ServicePlanRequest;
import com.fennec.fennecgo.dto.response.ServicePlanResponse;
import com.fennec.fennecgo.dto.response.TransactionTypeResponse;
import com.fennec.fennecgo.services.Interface.ServicePlanService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ServicePlanController {
    private final ServicePlanService svc;

    @GetMapping("/service-plans")
    public Page<ServicePlanResponse> list(
        @RequestParam(required = false) Long serviceProviderId,
        @RequestParam(required = false) Long transactionTypeId,
        @PageableDefault(size = 20) Pageable page
    ) {
        return svc.listServicePlans(serviceProviderId, transactionTypeId, page);
    }

    @GetMapping("/service-providers/{providerId}/plans")
    public Page<ServicePlanResponse> listByProvider(
        @PathVariable Long providerId,
        @RequestParam(required = false) Long transactionTypeId,
        @PageableDefault(size = 20) Pageable page
    ) {
        return svc.listServicePlans(providerId, transactionTypeId, page);
    }

    @GetMapping("/service-plans/{id}")
    public ServicePlanResponse getOne(@PathVariable Long id) {
        return svc.getServicePlanById(id);
    }

    @PostMapping("/service-plans")
    @ResponseStatus(HttpStatus.CREATED)
    public ServicePlanResponse create(@RequestBody ServicePlanRequest req) {
        return svc.createServicePlan(req);
    }

    @PutMapping("/service-plans/{id}")
    public ServicePlanResponse update(
        @PathVariable Long id,
        @RequestBody ServicePlanRequest req
    ) {
        return svc.updateServicePlan(id, req);
    }

    @DeleteMapping("/service-plans/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        svc.deleteServicePlan(id);
    }
    @GetMapping("/service-providers/{providerId}/transaction-types")
    public List<TransactionTypeResponse> getTxTypesByProvider(
            @PathVariable Long providerId
    ) {
        return svc.listTransactionTypesByProvider(providerId);
    }
}
