// src/main/java/com/fennec/fennecgo/mapper/ServicePlanMapper.java
package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.request.ServicePlanRequest;
import com.fennec.fennecgo.dto.response.ServicePlanResponse;
import com.fennec.fennecgo.models.ServicePlan;
import org.springframework.stereotype.Component;

@Component
public class ServicePlanMapper {

    public ServicePlanResponse toResponse(ServicePlan sp) {
        ServicePlanResponse res = new ServicePlanResponse();
        res.setId(sp.getId());
        res.setServiceProviderId(sp.getServiceProvider().getId());
        res.setTransactionTypeId(sp.getTransactionType().getId());
        res.setPlanName(sp.getPlanName());
        res.setPrice(sp.getPrice());
        res.setValidityDays(sp.getValidityDays());
        res.setDataVolume(sp.getDataVolume());
        res.setCallMinutes(sp.getCallMinutes());
        res.setDescription(sp.getDescription());
        res.setCreatedAt(sp.getCreatedAt());
        res.setUpdatedAt(sp.getUpdatedAt());
        return res;
    }
}
