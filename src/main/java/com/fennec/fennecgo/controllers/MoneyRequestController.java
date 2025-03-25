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

    // Create a money request (requestor's ID is derived from security context)
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

    // Get all money requests for the current user (as either requestor or payer)
    @GetMapping
    public ResponseEntity<List<MoneyRequestResponse>> getMyMoneyRequests() {
        List<MoneyRequestResponse> responses = moneyRequestService.getRequestsForUser();
        return ResponseEntity.ok(responses);
    }

    // Get money requests where the current user is the payer
    @GetMapping("/payer")
    public ResponseEntity<List<MoneyRequestResponse>> getMoneyRequestsForPayer() {
        List<MoneyRequestResponse> responses = moneyRequestService.getRequestsForPayer();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/cancel/{requestId}")
    public ResponseEntity<Void> cancelMoneyRequest(@PathVariable Long requestId) {
        moneyRequestService.cancelMoneyRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/confirm/{requestId}")
    public ResponseEntity<MoneyRequestResponse> confirmMoneyRequest(@PathVariable Long requestId) {
        MoneyRequestResponse response = moneyRequestService.confirmMoneyRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/decline/{requestId}")
    public ResponseEntity<MoneyRequestResponse> declineMoneyRequest(@PathVariable Long requestId) {
        MoneyRequestResponse response = moneyRequestService.declineMoneyRequest(requestId);
        return ResponseEntity.ok(response);
    }
}
