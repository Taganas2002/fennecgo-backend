package com.fennec.fennecgo.repository;

import com.fennec.fennecgo.models.FeeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeRuleRepository extends JpaRepository<FeeRule, Long> {
	List<FeeRule> findByTransactionTypeIdAndActiveTrue(Long transactionTypeId);
    List<FeeRule> findByTransactionTypeId(Long transactionTypeId);
}
