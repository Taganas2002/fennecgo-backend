package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.WalletTransferRequest;
import com.fennec.fennecgo.dto.response.WalletTransferResponse;

public interface WalletTransferService {
    WalletTransferResponse transferNow(WalletTransferRequest request);
}
