package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.TransactionTypeRequest;
import com.fennec.fennecgo.dto.response.TransactionTypeResponse;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.TransactionType;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.services.Interface.TransactionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionTypeServiceImpl implements TransactionTypeService {

    private final TransactionTypeRepository transactionTypeRepository;

    @Override
    public TransactionTypeResponse createTransactionType(TransactionTypeRequest request) {
        TransactionType entity = TransactionType.builder()
                .code(request.getCode())
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .build();
        transactionTypeRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    public List<TransactionTypeResponse> getAllTransactionTypes() {
        return transactionTypeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionTypeResponse getTransactionTypeById(Long id) {
        TransactionType entity = transactionTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransactionType not found with id: " + id));
        return toResponse(entity);
    }

    @Override
    public TransactionTypeResponse updateTransactionType(Long id, TransactionTypeRequest request) {
        TransactionType entity = transactionTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TransactionType not found with id: " + id));

        entity.setCode(request.getCode());
        entity.setName(request.getName());
        entity.setCategory(request.getCategory());
        entity.setDescription(request.getDescription());

        transactionTypeRepository.save(entity);
        return toResponse(entity);
    }

    @Override
    public void deleteTransactionType(Long id) {
        if (!transactionTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("TransactionType not found with id: " + id);
        }
        transactionTypeRepository.deleteById(id);
    }

    private TransactionTypeResponse toResponse(TransactionType entity) {
        TransactionTypeResponse response = new TransactionTypeResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setCategory(entity.getCategory());
        response.setDescription(entity.getDescription());
        return response;
    }
}
