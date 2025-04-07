package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.request.ServicePlanRequest;
import com.fennec.fennecgo.dto.response.ServicePlanResponse;
import com.fennec.fennecgo.models.ServicePlan;
import com.fennec.fennecgo.models.ServiceProvider;
import com.fennec.fennecgo.models.TransactionType;
import com.fennec.fennecgo.repository.ServiceProviderRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// SLF4J logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class ServicePlanMapper {

    private static final Logger logger = LoggerFactory.getLogger(ServicePlanMapper.class);

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    public ServicePlan toEntity(ServicePlanRequest request) {
        logger.info("Mapping ServicePlanRequest to ServicePlan entity");

        // 1) Fetch ServiceProvider, if provided
        ServiceProvider provider = null;
        if (request.getServiceProviderId() != null) {
            logger.debug("Looking up ServiceProvider with ID: {}", request.getServiceProviderId());
            Optional<ServiceProvider> optProvider = serviceProviderRepository.findById(request.getServiceProviderId());
            if (optProvider.isEmpty()) {
                throw new RuntimeException("ServiceProvider not found with id: " + request.getServiceProviderId());
            }
            provider = optProvider.get();
        }

        // 2) Fetch TransactionType, if provided
        TransactionType txType = null;
        if (request.getTransactionTypeId() != null) {
            logger.debug("Looking up TransactionType with ID: {}", request.getTransactionTypeId());
            Optional<TransactionType> optTxType = transactionTypeRepository.findById(request.getTransactionTypeId());
            if (optTxType.isEmpty()) {
                throw new RuntimeException("TransactionType not found with id: " + request.getTransactionTypeId());
            }
            txType = optTxType.get();
        }

        // 3) Build the ServicePlan entity
        ServicePlan plan = ServicePlan.builder()
                .serviceProvider(provider)
                .transactionType(txType) // <-- Must pass the entity, not just the ID
                .planName(request.getPlanName())
                .price(request.getPrice())
                .validityDays(request.getValidityDays())
                .dataVolume(request.getDataVolume())
                .callMinutes(request.getCallMinutes())
                .description(request.getDescription())
                .build();

        logger.debug("Created ServicePlan entity: {}", plan);
        return plan;
    }

    public ServicePlanResponse toResponse(ServicePlan entity) {
        if (entity == null) {
            logger.warn("ServicePlan entity is null, returning null response");
            return null;
        }

        logger.info("Mapping ServicePlan entity to ServicePlanResponse DTO");
        ServicePlanResponse response = new ServicePlanResponse();
        response.setId(entity.getId());

        // If serviceProvider is loaded, set its ID
        if (entity.getServiceProvider() != null) {
            response.setServiceProviderId(entity.getServiceProvider().getId());
        }

        // If transactionType is loaded, set its ID
        if (entity.getTransactionType() != null) {
            response.setTransactionTypeId(entity.getTransactionType().getId());
        }

        response.setPlanName(entity.getPlanName());
        response.setPrice(entity.getPrice());
        response.setValidityDays(entity.getValidityDays());
        response.setDataVolume(entity.getDataVolume());
        response.setCallMinutes(entity.getCallMinutes());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());

        logger.debug("Created ServicePlanResponse DTO: {}", response);
        return response;
    }
}
