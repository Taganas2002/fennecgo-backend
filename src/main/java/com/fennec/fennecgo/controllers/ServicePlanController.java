package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.ServicePlanRequest;
import com.fennec.fennecgo.dto.response.ServicePlanResponse;
import com.fennec.fennecgo.services.Interface.ServicePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing ServicePlan CRUD.
 */
@RestController
@RequestMapping("/api/service-plans")
public class ServicePlanController {

    @Autowired
    private ServicePlanService servicePlanService;

    // CREATE
    @PostMapping
    public ServicePlanResponse create(@RequestBody ServicePlanRequest request) {
        return servicePlanService.createServicePlan(request);
    }

    // READ (All)
    @GetMapping
    public List<ServicePlanResponse> findAll() {
        return servicePlanService.getAllServicePlans();
    }

    // READ (One)
    @GetMapping("/{id}")
    public ServicePlanResponse findById(@PathVariable Long id) {
        return servicePlanService.getServicePlanById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ServicePlanResponse update(@PathVariable Long id,
                                      @RequestBody ServicePlanRequest request) {
        return servicePlanService.updateServicePlan(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        servicePlanService.deleteServicePlan(id);
    }
}
