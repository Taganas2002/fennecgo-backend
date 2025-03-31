package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.WalletTransferRequest;
import com.fennec.fennecgo.dto.response.TransferInitiationResponse;
import com.fennec.fennecgo.dto.response.TransferFinalResponse;
import com.fennec.fennecgo.services.Interface.WalletTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/p2p")
@RequiredArgsConstructor
public class WalletTransferController {

    private final WalletTransferService walletTransferService;

    // Step 1: Initiate the transfer in PENDING state
    @PostMapping("/initiate")
    public ResponseEntity<TransferInitiationResponse> initiate(@RequestBody WalletTransferRequest request) {
        TransferInitiationResponse response = walletTransferService.initiateTransfer(request);
        return ResponseEntity.ok(response);
    }

    // Step 2: Confirm the transfer by reference number
    @PatchMapping("/confirm/{referenceNumber}")
    public ResponseEntity<TransferFinalResponse> confirm(@PathVariable String referenceNumber) {
        TransferFinalResponse response = walletTransferService.confirmTransfer(referenceNumber);
        return ResponseEntity.ok(response);
    }
}
