package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.ServiceProviderRequest;
import com.fennec.fennecgo.dto.response.ServiceProviderResponse;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.ServiceCategory;
import com.fennec.fennecgo.models.ServiceProvider;
import com.fennec.fennecgo.repository.ServiceCategoryRepository;
import com.fennec.fennecgo.repository.ServiceProviderRepository;
import com.fennec.fennecgo.services.Interface.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceProviderImpl implements ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    public ServiceProviderResponse createBiller(ServiceProviderRequest request) {
        // Fetch the ServiceCategory entity using the provided categoryId
        ServiceCategory category = serviceCategoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Service Category not found with id: " + request.getCategory()));

        ServiceProvider serviceProvider = ServiceProvider.builder()
                .externalCode(request.getExternalCode())
                .name(request.getName())
                .category(category)
                .logoUrl(request.getLogoUrl())
                .description(request.getDescription())
                .build();

        serviceProviderRepository.save(serviceProvider);
        return toResponse(serviceProvider);
    }

    @Override
    public List<ServiceProviderResponse> getAllBillers() {
        return serviceProviderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServiceProviderResponse getBillerById(Long id) {
        ServiceProvider serviceProvider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found with id: " + id));
        return toResponse(serviceProvider);
    }

    @Override
    public ServiceProviderResponse updateBiller(Long id, ServiceProviderRequest request) {
        ServiceProvider serviceProvider = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found with id: " + id));

        // Fetch updated ServiceCategory from categoryId
        ServiceCategory category = serviceCategoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new ResourceNotFoundException("Service Category not found with id: " + request.getCategory()));

        // Update fields
        serviceProvider.setExternalCode(request.getExternalCode());
        serviceProvider.setName(request.getName());
        serviceProvider.setCategory(category);
        serviceProvider.setLogoUrl(request.getLogoUrl());
        serviceProvider.setDescription(request.getDescription());

        serviceProviderRepository.save(serviceProvider);
        return toResponse(serviceProvider);
    }

    @Override
    public void deleteBiller(Long id) {
        if (!serviceProviderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service Provider not found with id: " + id);
        }
        serviceProviderRepository.deleteById(id);
    }

    // Convert ServiceProvider entity to response DTO.
    private ServiceProviderResponse toResponse(ServiceProvider serviceProvider) {
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setId(serviceProvider.getId());
        response.setExternalCode(serviceProvider.getExternalCode());
        response.setName(serviceProvider.getName());
        // For simplicity, we return the category name as a String.
        response.setCategory(serviceProvider.getCategory().getName());
        response.setLogoUrl(serviceProvider.getLogoUrl());
        response.setDescription(serviceProvider.getDescription());
        return response;
    }
}
