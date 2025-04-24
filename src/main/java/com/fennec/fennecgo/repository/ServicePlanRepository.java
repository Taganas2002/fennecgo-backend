// src/main/java/com/fennec/fennecgo/repository/ServicePlanRepository.java
package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.ServicePlan;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fennec.fennecgo.models.TransactionType;

public interface ServicePlanRepository extends JpaRepository<ServicePlan, Long> {
    Page<ServicePlan> findAll(Pageable pageable);
    Page<ServicePlan> findByServiceProviderId(Long providerId, Pageable pageable);
    Page<ServicePlan> findByTransactionTypeId(Long txTypeId, Pageable pageable);
    Page<ServicePlan> findByServiceProviderIdAndTransactionTypeId(Long providerId, Long txTypeId, Pageable pageable);
    @Query("""
    	      SELECT DISTINCT sp.transactionType 
    	      FROM ServicePlan sp 
    	      WHERE sp.serviceProvider.id = :providerId
    	    """)
    	    List<TransactionType> findTransactionTypesByProviderId(@Param("providerId") Long providerId);
}
