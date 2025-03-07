package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.response.WalletResponse;
import com.fennec.fennecgo.models.Wallet;

public interface WalletService {
	WalletResponse createDefaultWalletForUser(Long userId);

    WalletResponse getWalletById(Long walletId);

    Wallet getWalletEntityByUserId(Long userId);
    
    WalletResponse getWalletForCurrentUser(Long userId);
  
}
