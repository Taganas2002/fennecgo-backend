package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.request.ServiceCategoryRequest;
import com.fennec.fennecgo.dto.response.ServiceCategoryResponse;
import com.fennec.fennecgo.models.ServiceCategory;
import org.springframework.stereotype.Component;

@Component
public class ServiceCategoryMapper {

    public ServiceCategoryResponse toResponse(ServiceCategory category) {
        ServiceCategoryResponse response = new ServiceCategoryResponse();
        response.setId(category.getId());
        response.setCode(category.getCode());
        response.setName(category.getName());
        response.setIconUrl(category.getIconUrl());
        return response;
    }

    public ServiceCategory toEntity(ServiceCategoryRequest request) {
        return ServiceCategory.builder()
                .code(request.getCode())
                .name(request.getName())
                .iconUrl(request.getIconUrl())
                .build();
    }
}
