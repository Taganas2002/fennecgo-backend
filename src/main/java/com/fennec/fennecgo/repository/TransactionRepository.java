package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.Transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Additional query methods if needed:
     List<Transaction> findByStatus(String status);
     List<Transaction> findByFromWalletId(Long walletId);
     Optional<Transaction> findByReferenceNumber(String referenceNumber);
}
