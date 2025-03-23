package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.DepositRequest;
import com.fennec.fennecgo.dto.response.DepositInitiationResponse;
import com.fennec.fennecgo.dto.response.DepositResponse;

public interface DepositService {

    // 1) Initiate deposit, returning a PENDING transaction
    DepositInitiationResponse initiateDeposit(DepositRequest request);


	DepositResponse confirmDepositByReference(String referenceNumber);

	DepositResponse cancelDepositByReference(String referenceNumber);
}
