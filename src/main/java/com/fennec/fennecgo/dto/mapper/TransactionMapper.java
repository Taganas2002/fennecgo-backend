package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.request.TransactionRequest;
import com.fennec.fennecgo.dto.response.TransactionResponse;
import com.fennec.fennecgo.models.*;
import com.fennec.fennecgo.repository.BillerRepository;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final TransactionTypeRepository transactionTypeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final WalletRepository walletRepository;
    private final BillerRepository billerRepository;

    public Transaction toEntity(TransactionRequest request) {
        Transaction transaction = new Transaction();

        // 1) Fetch and set TransactionType
        TransactionType transactionType = transactionTypeRepository
            .findById(request.getTransactionTypeId())
            .orElseThrow(() -> new RuntimeException("TransactionType not found"));
        transaction.setTransactionType(transactionType);

        // 2) Fetch and set PaymentMethod
        PaymentMethod paymentMethod = paymentMethodRepository
            .findById(request.getPaymentMethodId())
            .orElseThrow(() -> new RuntimeException("PaymentMethod not found"));
        transaction.setPaymentMethod(paymentMethod);

        // 3) Fetch and set fromWallet (if provided)
        if (request.getFromWalletId() != null) {
            Wallet fromWallet = walletRepository
                .findById(request.getFromWalletId())
                .orElseThrow(() -> new RuntimeException("FromWallet not found"));
            transaction.setFromWallet(fromWallet);
        }

        // 4) Fetch and set toWallet (if provided)
        if (request.getToWalletId() != null) {
            Wallet toWallet = walletRepository
                .findById(request.getToWalletId())
                .orElseThrow(() -> new RuntimeException("ToWallet not found"));
            transaction.setToWallet(toWallet);
        }

        // 5) Fetch and set Biller if provided
        if (request.getBillerId() != null) {
            Biller biller = billerRepository
                .findById(request.getBillerId())
                .orElseThrow(() -> new RuntimeException("Biller not found"));
            transaction.setBiller(biller);
        }

        // 6) Set other fields
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.PENDING);

        return transaction;
    }

    public TransactionResponse toResponse(Transaction entity) {
        TransactionResponse response = new TransactionResponse();
        
        // 1) Basic fields
        response.setTransactionId(entity.getId());
        response.setStatus(entity.getStatus().toString());
        response.setAmount(entity.getAmount());
        response.setFee(entity.getFee());
        response.setReferenceNumber(entity.getReferenceNumber());
        response.setCreatedAt(entity.getCreatedAt());

        // 2) Payment method name/type
        if (entity.getPaymentMethod() != null) {
            response.setPaymentMethod(entity.getPaymentMethod().getType()); 
        }

        // 3) If you want to show "totalCharge" as amount + fee
        if (entity.getAmount() != null && entity.getFee() != null) {
            response.setTotalCharge(entity.getAmount().add(entity.getFee()));
        }

        // 4) If you want a "newBalance" or "remainingBalance", 
        //    you typically compute that in your service after wallet updates.
        //    But if the transaction has a toWallet or fromWallet, you could do:
        /*
        if (entity.getToWallet() != null) {
            response.setNewBalance(entity.getToWallet().getBalance());
        }
        */
        // This depends on your design. Usually the service sets it explicitly.

        return response;
    }
}
