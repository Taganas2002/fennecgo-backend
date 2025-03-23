package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.BillerRequest;
import com.fennec.fennecgo.dto.response.BillerResponse;
import com.fennec.fennecgo.services.Interface.BillerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billers")
@RequiredArgsConstructor
public class BillerController {

    private final BillerService billerService;

    @PostMapping
    public ResponseEntity<BillerResponse> createBiller(@RequestBody BillerRequest request) {
        BillerResponse response = billerService.createBiller(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BillerResponse>> getAllBillers() {
        List<BillerResponse> list = billerService.getAllBillers();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillerResponse> getBillerById(@PathVariable Long id) {
        BillerResponse response = billerService.getBillerById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillerResponse> updateBiller(@PathVariable Long id, @RequestBody BillerRequest request) {
        BillerResponse response = billerService.updateBiller(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBiller(@PathVariable Long id) {
        billerService.deleteBiller(id);
        return ResponseEntity.noContent().build();
    }
}
