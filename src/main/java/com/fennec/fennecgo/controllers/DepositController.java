package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.DepositRequest;
import com.fennec.fennecgo.dto.response.DepositInitiationResponse;
import com.fennec.fennecgo.dto.response.DepositResponse;
import com.fennec.fennecgo.services.Interface.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/initiate")
    public ResponseEntity<DepositInitiationResponse> initiateDeposit(@RequestBody DepositRequest request) {
        DepositInitiationResponse response = depositService.initiateDeposit(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cancel/{reference}")
    public ResponseEntity<DepositResponse> cancelDeposit(@PathVariable("reference") String referenceNumber) {
        DepositResponse response = depositService.cancelDepositByReference(referenceNumber);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/confirm/{reference}")
    public ResponseEntity<DepositResponse> confirmDeposit(@PathVariable("reference") String referenceNumber) {
        DepositResponse response = depositService.confirmDepositByReference(referenceNumber);
        return ResponseEntity.ok(response);
    }
}
