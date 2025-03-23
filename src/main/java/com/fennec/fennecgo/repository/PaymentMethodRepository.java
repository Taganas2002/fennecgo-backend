package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.PaymentMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    // Case-insensitive lookup by 'type'
    Optional<PaymentMethod> findByTypeIgnoreCase(String type);

}

