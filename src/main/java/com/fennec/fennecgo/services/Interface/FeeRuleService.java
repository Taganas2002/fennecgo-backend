package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.dto.request.FeeRuleRequest;
import com.fennec.fennecgo.dto.response.FeeRuleResponse;

import java.util.List;

public interface FeeRuleService {
    FeeRuleResponse createFeeRule(FeeRuleRequest request);
    List<FeeRuleResponse> getAllFeeRules();
    FeeRuleResponse getFeeRuleById(Long id);
    FeeRuleResponse updateFeeRule(Long id, FeeRuleRequest request);
    void deleteFeeRule(Long id);

    /**
     * For calculating fees in deposit flows, etc.
     */
    java.math.BigDecimal calculateFee(
        com.fennec.fennecgo.models.TransactionType transactionType,
        java.math.BigDecimal amount
    );
}
