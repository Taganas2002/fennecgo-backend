package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import com.fennec.fennecgo.services.Interface.MoneyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/money-requests")
@RequiredArgsConstructor
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    @PostMapping("/create")
    public ResponseEntity<MoneyRequestResponse> createMoneyRequest(@RequestBody MoneyRequestRequest request) {
        MoneyRequestResponse response = moneyRequestService.createMoneyRequest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoneyRequestResponse> getMoneyRequestById(@PathVariable Long id) {
        MoneyRequestResponse response = moneyRequestService.getMoneyRequestById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MoneyRequestResponse>> getMyMoneyRequests() {
        List<MoneyRequestResponse> responses = moneyRequestService.getRequestsForUser();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/payer")
    public ResponseEntity<List<MoneyRequestResponse>> getMoneyRequestsForPayer() {
        List<MoneyRequestResponse> responses = moneyRequestService.getRequestsForPayer();
        return ResponseEntity.ok(responses);
    }

    // Updated endpoints to use referenceNumber as path variable.
    @PatchMapping("/cancel/{referenceNumber}")
    public ResponseEntity<MoneyRequestResponse> cancelMoneyRequest(@PathVariable String referenceNumber) {
        MoneyRequestResponse response = moneyRequestService.cancelMoneyRequest(referenceNumber);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/confirm/{referenceNumber}")
    public ResponseEntity<MoneyRequestResponse> confirmMoneyRequest(@PathVariable String referenceNumber) {
        MoneyRequestResponse response = moneyRequestService.confirmMoneyRequest(referenceNumber);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/decline/{referenceNumber}")
    public ResponseEntity<MoneyRequestResponse> declineMoneyRequest(@PathVariable String referenceNumber) {
        MoneyRequestResponse response = moneyRequestService.declineMoneyRequest(referenceNumber);
        return ResponseEntity.ok(response);
    }
}
