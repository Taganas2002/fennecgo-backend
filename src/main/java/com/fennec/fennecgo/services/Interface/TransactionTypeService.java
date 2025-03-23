package com.fennec.fennecgo.services.Interface;

import java.util.List;

import com.fennec.fennecgo.dto.request.TransactionTypeRequest;
import com.fennec.fennecgo.dto.response.TransactionTypeResponse;

public interface TransactionTypeService {
	TransactionTypeResponse createTransactionType(TransactionTypeRequest request);
    List<TransactionTypeResponse> getAllTransactionTypes();
    TransactionTypeResponse getTransactionTypeById(Long id);
    TransactionTypeResponse updateTransactionType(Long id, TransactionTypeRequest request);
    void deleteTransactionType(Long id);
}
