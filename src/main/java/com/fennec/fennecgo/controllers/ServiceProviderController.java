package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.ServiceProviderRequest;
import com.fennec.fennecgo.dto.response.ServiceProviderResponse;
import com.fennec.fennecgo.services.Interface.ServiceProviderService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-providers")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    @PostMapping
    public ResponseEntity<ServiceProviderResponse> createBiller(@RequestBody ServiceProviderRequest request) {
    	ServiceProviderResponse response = serviceProviderService.createBiller(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ServiceProviderResponse>> getAllBillers() {
        List<ServiceProviderResponse> list = serviceProviderService.getAllBillers();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceProviderResponse> getBillerById(@PathVariable Long id) {
    	ServiceProviderResponse response = serviceProviderService.getBillerById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceProviderResponse> updateBiller(@PathVariable Long id, @RequestBody ServiceProviderRequest request) {
    	ServiceProviderResponse response = serviceProviderService.updateBiller(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBiller(@PathVariable Long id) {
    	serviceProviderService.deleteBiller(id);
        return ResponseEntity.noContent().build();
    }
}
