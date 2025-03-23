package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.WithdrawalRequest;
import com.fennec.fennecgo.dto.response.WithdrawalInitiationResponse;
import com.fennec.fennecgo.dto.response.WithdrawalResponse;
import com.fennec.fennecgo.services.Interface.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping("/initiate")
    public ResponseEntity<WithdrawalInitiationResponse> initiateWithdrawal(@RequestBody WithdrawalRequest request) {
        WithdrawalInitiationResponse response = withdrawalService.initiateWithdrawal(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/cancel/{reference}")
    public ResponseEntity<WithdrawalResponse> cancelWithdrawal(@PathVariable("reference") String reference) {
        WithdrawalResponse response = withdrawalService.cancelWithdrawalByReference(reference);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/confirm/{reference}")
    public ResponseEntity<WithdrawalResponse> confirmWithdrawal(@PathVariable("reference") String reference) {
        WithdrawalResponse response = withdrawalService.confirmWithdrawalByReference(reference);
        return ResponseEntity.ok(response);
    }
}
