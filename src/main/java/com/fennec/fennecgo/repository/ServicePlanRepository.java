package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.ServicePlan;
import com.fennec.fennecgo.models.TransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePlanRepository extends JpaRepository<ServicePlan, Long> {

	// Find all ServicePlans by their TransactionType entity
    List<ServicePlan> findByTransactionType(TransactionType transactionType);

    // Or, find by the transactionType's ID (using a custom JPQL query):
    @Query("SELECT sp FROM ServicePlan sp WHERE sp.transactionType.id = :txTypeId")
    List<ServicePlan> findByTransactionTypeId(@Param("txTypeId") Long txTypeId);

    // Example: find all plans by serviceProviderId
    List<ServicePlan> findByServiceProviderId(Long providerId);
}
