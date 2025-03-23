package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.TransactionTypeRequest;
import com.fennec.fennecgo.dto.response.TransactionTypeResponse;
import com.fennec.fennecgo.services.Interface.TransactionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-types")
@RequiredArgsConstructor
public class TransactionTypeController {

    private final TransactionTypeService transactionTypeService;

    @PostMapping
    public ResponseEntity<TransactionTypeResponse> createTransactionType(@RequestBody TransactionTypeRequest request) {
        TransactionTypeResponse response = transactionTypeService.createTransactionType(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionTypeResponse>> getAllTransactionTypes() {
        List<TransactionTypeResponse> list = transactionTypeService.getAllTransactionTypes();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionTypeResponse> getTransactionTypeById(@PathVariable Long id) {
        TransactionTypeResponse response = transactionTypeService.getTransactionTypeById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionTypeResponse> updateTransactionType(@PathVariable Long id,
                                                                         @RequestBody TransactionTypeRequest request) {
        TransactionTypeResponse response = transactionTypeService.updateTransactionType(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionType(@PathVariable Long id) {
        transactionTypeService.deleteTransactionType(id);
        return ResponseEntity.noContent().build();
    }
}
