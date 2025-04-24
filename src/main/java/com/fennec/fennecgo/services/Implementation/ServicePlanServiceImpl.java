package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.ServicePlanRequest;
import com.fennec.fennecgo.dto.response.ServicePlanResponse;
import com.fennec.fennecgo.dto.response.TransactionTypeResponse;
import com.fennec.fennecgo.dto.mapper.ServicePlanMapper;
import com.fennec.fennecgo.models.ServicePlan;
import com.fennec.fennecgo.repository.ServicePlanRepository;
import com.fennec.fennecgo.repository.ServiceProviderRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.services.Interface.ServicePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.fennec.fennecgo.models.TransactionType;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicePlanServiceImpl implements ServicePlanService {
    private final ServicePlanRepository repo;
    private final ServiceProviderRepository providerRepo;
    private final TransactionTypeRepository txRepo;
    private final ServicePlanMapper mapper;

    @Override
    public ServicePlanResponse createServicePlan(ServicePlanRequest req) {
        ServicePlan sp = new ServicePlan();
        sp.setServiceProvider(providerRepo.findById(req.getServiceProviderId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found")));
        sp.setTransactionType(txRepo.findById(req.getTransactionTypeId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Transaction type not found")));
        sp.setPlanName(req.getPlanName());
        sp.setPrice(req.getPrice());
        sp.setValidityDays(req.getValidityDays());
        sp.setDataVolume(req.getDataVolume());
        sp.setCallMinutes(req.getCallMinutes());
        sp.setDescription(req.getDescription());
        ServicePlan saved = repo.save(sp);
        return mapper.toResponse(saved);
    }

    @Override
    public ServicePlanResponse getServicePlanById(Long id) {
        ServicePlan sp = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "ServicePlan not found"));
        return mapper.toResponse(sp);
    }

    @Override
    public ServicePlanResponse updateServicePlan(Long id, ServicePlanRequest req) {
        ServicePlan sp = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "ServicePlan not found"));
        sp.setServiceProvider(providerRepo.findById(req.getServiceProviderId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provider not found")));
        sp.setTransactionType(txRepo.findById(req.getTransactionTypeId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Transaction type not found")));
        sp.setPlanName(req.getPlanName());
        sp.setPrice(req.getPrice());
        sp.setValidityDays(req.getValidityDays());
        sp.setDataVolume(req.getDataVolume());
        sp.setCallMinutes(req.getCallMinutes());
        sp.setDescription(req.getDescription());
        ServicePlan updated = repo.save(sp);
        return mapper.toResponse(updated);
    }

    @Override
    public void deleteServicePlan(Long id) {
        if (!repo.existsById(id)) 
            throw new ResponseStatusException(NOT_FOUND, "ServicePlan not found");
        repo.deleteById(id);
    }

    @Override
    public Page<ServicePlanResponse> listServicePlans(Long providerId, Long txTypeId, Pageable page) {
        Page<ServicePlan> ents;
        if (providerId != null && txTypeId != null) {
            ents = repo.findByServiceProviderIdAndTransactionTypeId(providerId, txTypeId, page);
        } else if (providerId != null) {
            ents = repo.findByServiceProviderId(providerId, page);
        } else if (txTypeId != null) {
            ents = repo.findByTransactionTypeId(txTypeId, page);
        } else {
            ents = repo.findAll(page);
        }
        return ents.map(mapper::toResponse);
    }
    @Override
    public List<TransactionTypeResponse> listTransactionTypesByProvider(Long providerId) {
        List<TransactionType> types = repo.findTransactionTypesByProviderId(providerId);
        return types.stream().map(tx -> {
            TransactionTypeResponse dto = new TransactionTypeResponse();
            dto.setId(tx.getId());
            dto.setCode(tx.getCode());
            dto.setName(tx.getName());
            dto.setCategory(tx.getCategory());
            dto.setDescription(tx.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }
}
