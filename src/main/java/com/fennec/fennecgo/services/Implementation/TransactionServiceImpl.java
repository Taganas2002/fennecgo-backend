package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.DepositRequest;
import com.fennec.fennecgo.dto.request.TransactionRequest;
import com.fennec.fennecgo.dto.request.WithdrawalRequest;
import com.fennec.fennecgo.dto.response.MyActivityResponse;
import com.fennec.fennecgo.dto.response.TransactionResponse;
import com.fennec.fennecgo.exception.InsufficientBalanceException;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.*;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.repository.TransactionRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import com.fennec.fennecgo.security.services.UserDetailsImpl;
import com.fennec.fennecgo.services.Interface.FeeRuleService;
import com.fennec.fennecgo.services.Interface.TransactionService;
import com.fennec.fennecgo.dto.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    public MyActivityResponse getMyActivity() {
        // 1) Get current user ID from SecurityContext
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // 2) Fetch the user & all wallets belonging to them
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        List<Wallet> userWallets = walletRepository.findByUserId(user.getId());
        if (userWallets.isEmpty()) {
            // If user has no wallets, just return empty
            return new MyActivityResponse();
        }
        Set<Long> userWalletIds = userWallets.stream()
            .map(Wallet::getId)
            .collect(Collectors.toSet());

        // 3) Query all transactions where fromWallet or toWallet is in userWalletIds
        // -- Example: using findAll() and filter. 
        //    For production, a custom query is better for performance:
        //      transactionRepository.findByWalletIds(userWalletIds).
        List<Transaction> allTxns = transactionRepository.findAll().stream()
            .filter(txn -> {
                Long fromId = (txn.getFromWallet() != null) ? txn.getFromWallet().getId() : null;
                Long toId   = (txn.getToWallet()   != null) ? txn.getToWallet().getId()   : null;
                return (fromId != null && userWalletIds.contains(fromId))
                    || (toId   != null && userWalletIds.contains(toId));
            })
            .collect(Collectors.toList());

        // 4) Convert each Transaction to TransactionResponse
        List<TransactionResponse> allResponses = allTxns.stream()
            .map(txn -> transactionMapper.toResponse(txn, userWalletIds))
            .collect(Collectors.toList());

        // 5) Split into sent vs. received
        List<TransactionResponse> sentList = allResponses.stream()
            .filter(r -> "SENT".equals(r.getDirection()))
            .collect(Collectors.toList());

        List<TransactionResponse> receivedList = allResponses.stream()
            .filter(r -> "RECEIVED".equals(r.getDirection()))
            .collect(Collectors.toList());

        // 6) Build final response
        MyActivityResponse activity = new MyActivityResponse();
        activity.setAll(allResponses);
        activity.setSent(sentList);
        activity.setReceived(receivedList);

        return activity;
    }

    /**
     * Retrieves the current user ID from Spring Security’s context.
     */
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getId();
        }
        return null;
    }

	
}