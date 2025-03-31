package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.DepositRequest;
import com.fennec.fennecgo.dto.request.TransactionRequest;
import com.fennec.fennecgo.dto.request.WithdrawalRequest;
import com.fennec.fennecgo.dto.response.MyActivityResponse;
import com.fennec.fennecgo.dto.response.TransactionResponse;

public interface TransactionService {
	MyActivityResponse getMyActivity();
}

