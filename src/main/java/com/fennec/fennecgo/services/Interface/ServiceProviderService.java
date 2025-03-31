package com.fennec.fennecgo.services.Interface;

import java.util.List;

import com.fennec.fennecgo.dto.request.ServiceProviderRequest;
import com.fennec.fennecgo.dto.response.ServiceProviderResponse;

public interface ServiceProviderService {
	ServiceProviderResponse createBiller(ServiceProviderRequest request);
    List<ServiceProviderResponse> getAllBillers();
    ServiceProviderResponse getBillerById(Long id);
    ServiceProviderResponse updateBiller(Long id, ServiceProviderRequest request);
    void deleteBiller(Long id);
}
