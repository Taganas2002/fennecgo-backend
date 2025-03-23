package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.models.PaymentMethod;
import com.fennec.fennecgo.services.Implementation.PaymentMethodServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodServiceImpl paymentMethodServiceImpl;

    @PostMapping
    public ResponseEntity<PaymentMethod> createPaymentMethod(@RequestBody PaymentMethod paymentMethod) {
        PaymentMethod created = paymentMethodServiceImpl.createPaymentMethod(paymentMethod);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethod> getPaymentMethod(@PathVariable Long id) {
        PaymentMethod paymentMethod = paymentMethodServiceImpl.getPaymentMethod(id);
        return ResponseEntity.ok(paymentMethod);
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethod>> getAllPaymentMethods() {
        List<PaymentMethod> paymentMethods = paymentMethodServiceImpl.getAllPaymentMethods();
        return ResponseEntity.ok(paymentMethods);
    }
}
