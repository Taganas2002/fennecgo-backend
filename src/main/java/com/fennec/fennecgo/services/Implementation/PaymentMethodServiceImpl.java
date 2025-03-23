package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.models.PaymentMethod;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.services.Interface.PaymentMethodService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethod createPaymentMethod(PaymentMethod paymentMethod) {
        try {
            PaymentMethod created = paymentMethodRepository.save(paymentMethod);
            log.info("PaymentMethod created with id: {}", created.getId());
            return created;
        } catch (Exception ex) {
            log.error("Error creating PaymentMethod: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to create PaymentMethod", ex);
        }
    }

    public PaymentMethod getPaymentMethod(Long id) {
        try {
            PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("PaymentMethod with id {} not found", id);
                    return new RuntimeException("Payment method not found");
                });
            log.info("Fetched PaymentMethod with id: {}", id);
            return paymentMethod;
        } catch (Exception ex) {
            log.error("Error fetching PaymentMethod with id {}: {}", id, ex.getMessage(), ex);
            throw new RuntimeException("Failed to fetch PaymentMethod", ex);
        }
    }

    public List<PaymentMethod> getAllPaymentMethods() {
        try {
            List<PaymentMethod> methods = paymentMethodRepository.findAll();
            log.info("Fetched {} PaymentMethods", methods.size());
            return methods;
        } catch (Exception ex) {
            log.error("Error fetching all PaymentMethods: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to fetch PaymentMethods", ex);
        }
    }
}
