package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import com.fennec.fennecgo.security.services.UserDetailsImpl;
import com.fennec.fennecgo.services.Interface.MoneyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/money-requests")
@RequiredArgsConstructor
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    // Helper method to get the current authenticated user's ID
    private Long getCurrentUserId() {
        UserDetailsImpl userDetails =
            (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    // Create a new money request.
    @PostMapping
    public ResponseEntity<MoneyRequestResponse> createMoneyRequest(
            @RequestBody MoneyRequestRequest request) {
        Long requestorUserId = getCurrentUserId();
        MoneyRequestResponse response = moneyRequestService.createMoneyRequest(requestorUserId, request);
        return ResponseEntity.ok(response);
    }

    // Get money request by its ID.
    @GetMapping("/{id}")
    public ResponseEntity<MoneyRequestResponse> getMoneyRequestById(@PathVariable Long id) {
        MoneyRequestResponse response = moneyRequestService.getMoneyRequestById(id);
        return ResponseEntity.ok(response);
    }

    // Get all money requests related to the authenticated user (both as requestor and payer).
    @GetMapping
    public ResponseEntity<List<MoneyRequestResponse>> getRequestsForUser() {
        Long userId = getCurrentUserId();
        List<MoneyRequestResponse> responses = moneyRequestService.getRequestsForUser(userId);
        return ResponseEntity.ok(responses);
    }

    // Get only the money requests where the authenticated user is expected to pay.
    @GetMapping("/for-payer")
    public ResponseEntity<List<MoneyRequestResponse>> getRequestsForPayer() {
        Long userId = getCurrentUserId();
        List<MoneyRequestResponse> responses = moneyRequestService.getRequestsForPayer(userId);
        return ResponseEntity.ok(responses);
    }

    // Cancel a money request by ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelMoneyRequest(@PathVariable Long id) {
        moneyRequestService.cancelMoneyRequest(id);
        return ResponseEntity.noContent().build();
    }
}
