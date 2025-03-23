package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.FeeRuleRequest;
import com.fennec.fennecgo.dto.response.FeeRuleResponse;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.FeeRule;
import com.fennec.fennecgo.models.TransactionType;
import com.fennec.fennecgo.repository.FeeRuleRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.services.Interface.FeeRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeRuleServiceImpl implements FeeRuleService {

    private final FeeRuleRepository feeRuleRepository;
    private final TransactionTypeRepository transactionTypeRepository;

    @Override
    public FeeRuleResponse createFeeRule(FeeRuleRequest request) {
        TransactionType transactionType = transactionTypeRepository.findById(request.getTransactionTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("TransactionType not found with id: " + request.getTransactionTypeId()));

        FeeRule feeRule = FeeRule.builder()
                .transactionType(transactionType)
                .minAmount(request.getMinAmount())
                .maxAmount(request.getMaxAmount())
                .fixedFee(request.getFixedFee())
                .percentageFee(request.getPercentageFee())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                // If the request does not provide a value, default to false:
                .active(request.isActive()) // Ensure your request DTO has a boolean field, defaulting to false if not set.
                .build();


        feeRuleRepository.save(feeRule);
        return toResponse(feeRule);
    }

    @Override
    public List<FeeRuleResponse> getAllFeeRules() {
        return feeRuleRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FeeRuleResponse getFeeRuleById(Long id) {
        FeeRule feeRule = feeRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeRule not found with id: " + id));
        return toResponse(feeRule);
    }

    @Override
    public FeeRuleResponse updateFeeRule(Long id, FeeRuleRequest request) {
        FeeRule feeRule = feeRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeRule not found with id: " + id));

        // Possibly update transaction type if provided
        if (request.getTransactionTypeId() != null) {
            TransactionType transactionType = transactionTypeRepository.findById(request.getTransactionTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("TransactionType not found with id: " + request.getTransactionTypeId()));
            feeRule.setTransactionType(transactionType);
        }

        feeRule.setMinAmount(request.getMinAmount());
        feeRule.setMaxAmount(request.getMaxAmount());
        feeRule.setFixedFee(request.getFixedFee());
        feeRule.setPercentageFee(request.getPercentageFee());
        feeRule.setStartDate(request.getStartDate());
        feeRule.setEndDate(request.getEndDate());
        feeRule.setActive(request.isActive());

        feeRuleRepository.save(feeRule);
        return toResponse(feeRule);
    }

    @Override
    public void deleteFeeRule(Long id) {
        if (!feeRuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("FeeRule not found with id: " + id);
        }
        feeRuleRepository.deleteById(id);
    }

    /**
     * For deposit/transaction logic: calculates the fee by finding an active FeeRule 
     * that matches the given amount range. If none found, returns BigDecimal.ZERO.
     */
    @Override
    public BigDecimal calculateFee(TransactionType transactionType, BigDecimal amount) {
        List<FeeRule> rules = feeRuleRepository.findByTransactionTypeIdAndActiveTrue(transactionType.getId());
        System.out.println("Found " + rules.size() + " active fee rules for transaction type id " + transactionType.getId());
        for (FeeRule rule : rules) {
            System.out.println("Checking rule: min=" + rule.getMinAmount() + ", max=" + rule.getMaxAmount());
            if (amount.compareTo(rule.getMinAmount()) >= 0 && amount.compareTo(rule.getMaxAmount()) <= 0) {
                BigDecimal calculatedFee = rule.getFixedFee().add(amount.multiply(rule.getPercentageFee()));
                System.out.println("Fee calculated: " + calculatedFee);
                return calculatedFee;
            }
        }
        System.out.println("No matching fee rule found. Returning zero fee.");
        return BigDecimal.ZERO;
    }


    private FeeRuleResponse toResponse(FeeRule feeRule) {
        FeeRuleResponse response = new FeeRuleResponse();
        response.setId(feeRule.getId());
        response.setTransactionTypeId(feeRule.getTransactionType().getId());
        response.setMinAmount(feeRule.getMinAmount());
        response.setMaxAmount(feeRule.getMaxAmount());
        response.setFixedFee(feeRule.getFixedFee());
        response.setPercentageFee(feeRule.getPercentageFee());
        response.setStartDate(feeRule.getStartDate());
        response.setEndDate(feeRule.getEndDate());
        response.setActive(feeRule.isActive());
        return response;
    }
}
