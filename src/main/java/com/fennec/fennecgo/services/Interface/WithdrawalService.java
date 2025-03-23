package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.WithdrawalRequest;
import com.fennec.fennecgo.dto.response.WithdrawalInitiationResponse;
import com.fennec.fennecgo.dto.response.WithdrawalResponse;

public interface WithdrawalService {

    WithdrawalInitiationResponse initiateWithdrawal(WithdrawalRequest request);

    WithdrawalResponse cancelWithdrawalByReference(String referenceNumber);
    WithdrawalResponse confirmWithdrawalByReference(String referenceNumber);
}
