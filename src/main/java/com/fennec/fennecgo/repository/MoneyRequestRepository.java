package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.MoneyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {
    List<MoneyRequest> findByToWalletID_Id(Long walletId);
    List<MoneyRequest> findByFromWalletID_Id(Long walletId);
    List<MoneyRequest> findByFromWalletID_IdIn(List<Long> walletIds); 
    
    @Query("select m from MoneyRequest m where m.ReferenceNumber = ?1")
    Optional<MoneyRequest> findByReferenceNumber(String referenceNumber);
}
