package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.ServiceCategoryRequest;
import com.fennec.fennecgo.dto.response.ServiceCategoryResponse;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.ServiceCategory;
import com.fennec.fennecgo.repository.ServiceCategoryRepository;
import com.fennec.fennecgo.services.Interface.ServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    public ServiceCategoryResponse createServiceCategory(ServiceCategoryRequest request) {
        // Build entity
        ServiceCategory category = ServiceCategory.builder()
                .code(request.getCode())
                .name(request.getName())
                .iconUrl(request.getIconUrl()) // the file's URL if any
                .build();

        // Save
        serviceCategoryRepository.save(category);

        // Convert to response
        return toResponse(category);
    }

    @Override
    public List<ServiceCategoryResponse> getAllServiceCategories() {
        return serviceCategoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceCategoryResponse getServiceCategoryById(Long id) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service category not found with id: " + id));
        return toResponse(category);
    }

    @Override
    public ServiceCategoryResponse updateServiceCategory(Long id, ServiceCategoryRequest request) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service category not found with id: " + id));

        // Update fields
        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setIconUrl(request.getIconUrl());
        // Save
        serviceCategoryRepository.save(category);
        return toResponse(category);
    }

    @Override
    public void deleteServiceCategory(Long id) {
        if (!serviceCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service category not found with id: " + id);
        }
        serviceCategoryRepository.deleteById(id);
    }

    // Helper method to convert entity -> response
    private ServiceCategoryResponse toResponse(ServiceCategory category) {
        ServiceCategoryResponse resp = new ServiceCategoryResponse();
        resp.setId(category.getId());
        resp.setCode(category.getCode());
        resp.setName(category.getName());
        resp.setIconUrl(category.getIconUrl());
        return resp;
    }
}
