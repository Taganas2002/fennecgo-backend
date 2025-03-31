package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.ServiceCategoryRequest;
import com.fennec.fennecgo.dto.response.ServiceCategoryResponse;
import java.util.List;

public interface ServiceCategoryService {
    ServiceCategoryResponse createServiceCategory(ServiceCategoryRequest request);
    List<ServiceCategoryResponse> getAllServiceCategories();
    ServiceCategoryResponse getServiceCategoryById(Long id);
    ServiceCategoryResponse updateServiceCategory(Long id, ServiceCategoryRequest request);
    void deleteServiceCategory(Long id);
}
