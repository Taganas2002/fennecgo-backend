package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.WalletTransferRequest;
import com.fennec.fennecgo.dto.response.TransferInitiationResponse;
import com.fennec.fennecgo.dto.response.TransferFinalResponse;

public interface WalletTransferService {
    
    // Step 1: Initiate a transfer in PENDING state
    TransferInitiationResponse initiateTransfer(WalletTransferRequest request);

    // Step 2: Confirm the transfer by ID or reference number
    TransferFinalResponse confirmTransfer(String referenceNumber);
}
