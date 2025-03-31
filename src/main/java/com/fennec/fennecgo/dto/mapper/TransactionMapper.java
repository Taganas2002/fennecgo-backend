package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.request.TransactionRequest;
import com.fennec.fennecgo.dto.response.TransactionResponse;
import com.fennec.fennecgo.models.*;
import com.fennec.fennecgo.repository.ServiceProviderRepository;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class TransactionMapper {

    /**
     * Convert Transaction to TransactionResponse.
     *
     * @param txn The transaction entity
     * @param userWalletIds All wallet IDs owned by the current user
     * @return The response DTO
     */
    public TransactionResponse toResponse(Transaction txn, Set<Long> userWalletIds) {
        TransactionResponse resp = new TransactionResponse();
        resp.setId(txn.getId());
        resp.setAmount(txn.getAmount());
        resp.setDescription(txn.getDescription());
        resp.setStatus(txn.getStatus().name());
        resp.setCreatedAt(txn.getCreatedAt());

        // Determine direction (SENT or RECEIVED)
        Long fromWalletId = (txn.getFromWallet() != null) ? txn.getFromWallet().getId() : null;
        Long toWalletId   = (txn.getToWallet()   != null) ? txn.getToWallet().getId()   : null;

        if (fromWalletId != null && userWalletIds.contains(fromWalletId)) {
            resp.setDirection("SENT");
        } else if (toWalletId != null && userWalletIds.contains(toWalletId)) {
            resp.setDirection("RECEIVED");
        } else {
            resp.setDirection("UNKNOWN");
        }

        // Map fromUserName and toUserName if available
        if (txn.getFromWallet() != null && txn.getFromWallet().getUser() != null) {
            resp.setFromUserName(txn.getFromWallet().getUser().getUsername());
        }
        if (txn.getToWallet() != null && txn.getToWallet().getUser() != null) {
            resp.setToUserName(txn.getToWallet().getUser().getUsername());
        }

        // Map fee
        resp.setFee(txn.getFee());

        // Map payment method if available
        if (txn.getPaymentMethod() != null) {
            resp.setPaymentMethod(txn.getPaymentMethod().getType());
        } else {
            resp.setPaymentMethod(null);
        }

        // Map reference number
        resp.setReferenceNumber(txn.getReferenceNumber());

        // Calculate totalCharge: amount + fee (if fee is not null)
        if (txn.getFee() != null) {
            resp.setTotalCharge(txn.getAmount().add(txn.getFee()));
        } else {
            resp.setTotalCharge(txn.getAmount());
        }

        // Map transaction type (e.g., DEPOSIT, WITHDRAWAL, etc.)
        if (txn.getTransactionType() != null) {
            // Assuming TransactionType has a getCode() method; adjust if needed.
            resp.setTransactionType(txn.getTransactionType().getCode());
        } else {
            resp.setTransactionType(null);
        }

        // Map biller if available (assuming Biller has a getName() method)
        if (txn.getServiceProvider() != null) {
            resp.setServiceProvider(txn.getServiceProvider().getName());
        } else {
            resp.setServiceProvider(null);
        }

        return resp;
    }
}
