package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.mapper.ServicePlanMapper;
import com.fennec.fennecgo.dto.request.ServicePlanRequest;
import com.fennec.fennecgo.dto.response.ServicePlanResponse;
import com.fennec.fennecgo.models.ServicePlan;
import com.fennec.fennecgo.models.ServiceProvider;
import com.fennec.fennecgo.models.TransactionType;
import com.fennec.fennecgo.repository.ServicePlanRepository;
import com.fennec.fennecgo.repository.ServiceProviderRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.services.Interface.ServicePlanService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// SLF4J logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicePlanServiceImpl implements ServicePlanService {

    private static final Logger logger = LoggerFactory.getLogger(ServicePlanServiceImpl.class);

    @Autowired
    private ServicePlanRepository servicePlanRepository;

    @Autowired
    private ServicePlanMapper servicePlanMapper;

    // If you want to update the provider or transaction type in updateServicePlan,
    // you'll also need these repositories:
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Override
    public ServicePlanResponse createServicePlan(ServicePlanRequest request) {
        logger.info("Creating a new ServicePlan from request: {}", request);

        // Convert request -> entity
        ServicePlan plan = servicePlanMapper.toEntity(request);

        // Save
        ServicePlan saved = servicePlanRepository.save(plan);

        // Convert to response
        ServicePlanResponse response = servicePlanMapper.toResponse(saved);
        logger.info("Created ServicePlan with ID: {}", response.getId());
        return response;
    }

    @Override
    public List<ServicePlanResponse> getAllServicePlans() {
        logger.info("Fetching all ServicePlans");
        return servicePlanRepository.findAll()
                .stream()
                .map(servicePlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServicePlanResponse getServicePlanById(Long id) {
        logger.info("Fetching ServicePlan by ID: {}", id);
        ServicePlan plan = servicePlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServicePlan not found with id: " + id));
        return servicePlanMapper.toResponse(plan);
    }

    @Override
    public ServicePlanResponse updateServicePlan(Long id, ServicePlanRequest request) {
        logger.info("Updating ServicePlan with ID: {} using request: {}", id, request);

        // 1. Retrieve existing
        ServicePlan existing = servicePlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServicePlan not found with id: " + id));

        // 2. Update fields if present in the request
        // TransactionType ID
        if (request.getTransactionTypeId() != null) {
            logger.debug("Updating TransactionType to ID: {}", request.getTransactionTypeId());
            TransactionType txType = transactionTypeRepository.findById(request.getTransactionTypeId())
                    .orElseThrow(() -> new RuntimeException("TransactionType not found with id: " + request.getTransactionTypeId()));
            existing.setTransactionType(txType);
        }

        // planName
        if (request.getPlanName() != null) {
            logger.debug("Updating planName to {}", request.getPlanName());
            existing.setPlanName(request.getPlanName());
        }

        // price
        if (request.getPrice() != null) {
            logger.debug("Updating price to {}", request.getPrice());
            existing.setPrice(request.getPrice());
        }

        // validityDays
        if (request.getValidityDays() != null) {
            logger.debug("Updating validityDays to {}", request.getValidityDays());
            existing.setValidityDays(request.getValidityDays());
        }

        // dataVolume
        if (request.getDataVolume() != null) {
            logger.debug("Updating dataVolume to {}", request.getDataVolume());
            existing.setDataVolume(request.getDataVolume());
        }

        // callMinutes
        if (request.getCallMinutes() != null) {
            logger.debug("Updating callMinutes to {}", request.getCallMinutes());
            existing.setCallMinutes(request.getCallMinutes());
        }

        // description
        if (request.getDescription() != null) {
            logger.debug("Updating description to {}", request.getDescription());
            existing.setDescription(request.getDescription());
        }

        // ServiceProvider ID
        if (request.getServiceProviderId() != null) {
            logger.debug("Updating ServiceProvider to ID: {}", request.getServiceProviderId());
            ServiceProvider provider = serviceProviderRepository.findById(request.getServiceProviderId())
                    .orElseThrow(() -> new RuntimeException("ServiceProvider not found with id: " + request.getServiceProviderId()));
            existing.setServiceProvider(provider);
        }

        // 3. Save changes
        ServicePlan updated = servicePlanRepository.save(existing);

        ServicePlanResponse response = servicePlanMapper.toResponse(updated);
        logger.info("Updated ServicePlan with ID: {}", response.getId());
        return response;
    }

    @Override
    public void deleteServicePlan(Long id) {
        logger.info("Deleting ServicePlan with ID: {}", id);
        if (!servicePlanRepository.existsById(id)) {
            throw new RuntimeException("ServicePlan not found with id: " + id);
        }
        servicePlanRepository.deleteById(id);
        logger.info("ServicePlan with ID: {} was deleted successfully", id);
    }
}
