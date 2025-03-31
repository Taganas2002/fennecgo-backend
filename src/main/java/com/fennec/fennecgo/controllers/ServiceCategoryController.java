package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.ServiceCategoryRequest;
import com.fennec.fennecgo.dto.response.ServiceCategoryResponse;
import com.fennec.fennecgo.services.Interface.FileStorageService;
import com.fennec.fennecgo.services.Interface.ServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/service-categories")
@RequiredArgsConstructor
public class ServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;
    private final FileStorageService fileStorageService;

    // CREATE with image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceCategoryResponse> createServiceCategory(
            @RequestParam("code") String code,
            @RequestParam("name") String name,
            @RequestParam(value = "iconUrl", required = false) MultipartFile iconFile) {

        // 1) If file is provided, store it and get URL
        String iconUrl = null;
        if (iconFile != null && !iconFile.isEmpty()) {
            iconUrl = fileStorageService.storeFile(iconFile);
        }

        // 2) Build request object
        ServiceCategoryRequest request = new ServiceCategoryRequest();
        request.setCode(code);
        request.setName(name);
        request.setIconUrl(iconUrl);

        // 3) Pass to service
        ServiceCategoryResponse response = serviceCategoryService.createServiceCategory(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<ServiceCategoryResponse>> getAllServiceCategories() {
        List<ServiceCategoryResponse> responseList = serviceCategoryService.getAllServiceCategories();
        return ResponseEntity.ok(responseList);
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<ServiceCategoryResponse> getServiceCategoryById(@PathVariable Long id) {
        ServiceCategoryResponse response = serviceCategoryService.getServiceCategoryById(id);
        return ResponseEntity.ok(response);
    }

    // UPDATE (this version just uses JSON body, no file upload)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceCategoryResponse> updateServiceCategory(
            @PathVariable Long id,
            @RequestBody ServiceCategoryRequest request) {
        ServiceCategoryResponse response = serviceCategoryService.updateServiceCategory(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceCategory(@PathVariable Long id) {
        serviceCategoryService.deleteServiceCategory(id);
        return ResponseEntity.noContent().build();
    }
}
