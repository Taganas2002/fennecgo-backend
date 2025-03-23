package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.TransactionType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, Long> {

    // This tells Spring Data JPA to generate a query: SELECT ... WHERE code = :code
    Optional<TransactionType> findByCode(String code);
}

