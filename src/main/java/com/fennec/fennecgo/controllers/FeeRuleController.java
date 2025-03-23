package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.FeeRuleRequest;
import com.fennec.fennecgo.dto.response.FeeRuleResponse;
import com.fennec.fennecgo.services.Interface.FeeRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feerules")
@RequiredArgsConstructor
public class FeeRuleController {

    private final FeeRuleService feeRuleService;

    @PostMapping
    public ResponseEntity<FeeRuleResponse> createFeeRule(@RequestBody FeeRuleRequest request) {
        FeeRuleResponse response = feeRuleService.createFeeRule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FeeRuleResponse>> getAllFeeRules() {
        List<FeeRuleResponse> list = feeRuleService.getAllFeeRules();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeeRuleResponse> getFeeRuleById(@PathVariable Long id) {
        FeeRuleResponse response = feeRuleService.getFeeRuleById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeeRuleResponse> updateFeeRule(@PathVariable Long id, @RequestBody FeeRuleRequest request) {
        FeeRuleResponse response = feeRuleService.updateFeeRule(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeeRule(@PathVariable Long id) {
        feeRuleService.deleteFeeRule(id);
        return ResponseEntity.noContent().build();
    }
}
