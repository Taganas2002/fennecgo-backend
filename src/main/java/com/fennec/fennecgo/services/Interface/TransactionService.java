package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.DepositRequest;
import com.fennec.fennecgo.dto.request.TransactionRequest;
import com.fennec.fennecgo.dto.request.WithdrawalRequest;
import com.fennec.fennecgo.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    TransactionResponse deposit(DepositRequest request);
    TransactionResponse withdraw(WithdrawalRequest request);
    TransactionResponse getTransactionById(Long id);
}

