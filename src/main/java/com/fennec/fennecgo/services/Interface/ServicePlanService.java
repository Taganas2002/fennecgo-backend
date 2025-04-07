package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.ServicePlanRequest;
import com.fennec.fennecgo.dto.response.ServicePlanResponse;

import java.util.List;

public interface ServicePlanService {

	    ServicePlanResponse createServicePlan(ServicePlanRequest request);
	    List<ServicePlanResponse> getAllServicePlans();
	    ServicePlanResponse getServicePlanById(Long id);
	    ServicePlanResponse updateServicePlan(Long id, ServicePlanRequest request);
	    void deleteServicePlan(Long id);
}
