package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.WalletTransferRequest;
import com.fennec.fennecgo.dto.response.WalletTransferResponse;
import com.fennec.fennecgo.services.Interface.WalletTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet-transfer")
@RequiredArgsConstructor
public class WalletTransferController {

    private final WalletTransferService walletTransferService;

    @PostMapping("/P2P")
    public ResponseEntity<WalletTransferResponse> transfer(@RequestBody WalletTransferRequest request) {
        WalletTransferResponse response = walletTransferService.transferNow(request);
        return ResponseEntity.ok(response);
    }
}
