package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.WalletTransferRequest;
import com.fennec.fennecgo.dto.response.TransferInitiationResponse;
import com.fennec.fennecgo.dto.response.TransferFinalResponse;
import com.fennec.fennecgo.exception.InsufficientBalanceException;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.*;
import com.fennec.fennecgo.repository.*;
import com.fennec.fennecgo.security.services.UserDetailsImpl;
import com.fennec.fennecgo.services.Interface.FeeRuleService;
import com.fennec.fennecgo.services.Interface.WalletTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletTransferServiceImpl implements WalletTransferService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final FeeRuleService feeRuleService;

    @Override
    public TransferInitiationResponse initiateTransfer(WalletTransferRequest request) {
        // 1) Get sender's user ID from the Security Context (JWT)
        Long senderUserId = getCurrentUserId();
        if (senderUserId == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        // 2) Fetch sender's default wallet
        User senderUser = userRepository.findById(senderUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Sender not found with id: " + senderUserId));
        Wallet senderWallet = walletRepository.findDefaultWalletByUserId(senderUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Sender's default wallet not found"));

        // 3) Fetch receiver's default wallet using toUserId
        User receiverUser = userRepository.findById(request.getToUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + request.getToUserId()));
        Wallet receiverWallet = walletRepository.findDefaultWalletByUserId(receiverUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Receiver's default wallet not found"));

        // 4) Validate the transfer amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        // 5) Fetch transaction type "P2P_TRANSFER"
        TransactionType p2pType = transactionTypeRepository.findByCode("P2P_TRANSFER")
            .orElseThrow(() -> new ResourceNotFoundException("TransactionType 'P2P_TRANSFER' not found"));

        // 6) Fetch payment method "WALLET"
        PaymentMethod pm = paymentMethodRepository.findByTypeIgnoreCase("WALLET")
            .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod 'WALLET' not found"));

        // 7) Calculate fee
        BigDecimal fee = feeRuleService.calculateFee(p2pType, request.getAmount());

        // 8) Create a new Transaction with status = PENDING (no wallet update yet)
        Transaction txn = new Transaction();
        txn.setTransactionType(p2pType);
        txn.setFromWallet(senderWallet);
        txn.setToWallet(receiverWallet);
        txn.setPaymentMethod(pm);
        txn.setAmount(request.getAmount());
        txn.setFee(fee);
        txn.setStatus(TransactionStatus.PENDING);
        txn.setDescription(request.getDescription()); 
        String refNumber = "P2P_" + UUID.randomUUID();
        txn.setReferenceNumber(refNumber);

        // 9) Save the transaction
        transactionRepository.save(txn);

        // 10) Build and return the initiation response
        TransferInitiationResponse response = new TransferInitiationResponse();
        response.setTransactionId(txn.getId());
        response.setReferenceNumber(refNumber);
        response.setAmount(txn.getAmount());
        response.setFee(fee);
        response.setTotalCharge(txn.getAmount().add(fee));
        response.setPaymentMethod(pm.getType());
        response.setStatus(TransactionStatus.PENDING.name());
        response.setCreatedAt(txn.getCreatedAt());
        response.setDescription(request.getDescription());

        // Optional: set some sender info if desired
        response.setSenderUsername(senderUser.getUsername());
        response.setSenderEmail(senderUser.getEmail());

        return response;
    }

    @Override
    public TransferFinalResponse confirmTransfer(String referenceNumber) {
        // 1) Fetch the existing transaction by reference
        Transaction txn = transactionRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference: " + referenceNumber));

        // 2) Check if it's PENDING
        if (txn.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm a transaction that is not PENDING");
        }

        // 3) Check if the sender still has enough balance
        Wallet senderWallet = txn.getFromWallet();
        if (senderWallet.getBalance().compareTo(txn.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in sender's wallet");
        }

        // 4) Deduct from sender, credit receiver
        senderWallet.setBalance(senderWallet.getBalance().subtract(txn.getAmount()));
        walletRepository.save(senderWallet);

        Wallet receiverWallet = txn.getToWallet();
        receiverWallet.setBalance(receiverWallet.getBalance().add(txn.getAmount()));
        walletRepository.save(receiverWallet);

        // 5) Update transaction status to SUCCESS
        txn.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(txn);

        // 6) Build and return final response
        TransferFinalResponse response = new TransferFinalResponse();
        response.setTransactionId(txn.getId());
        response.setReferenceNumber(txn.getReferenceNumber());
        response.setAmount(txn.getAmount());
        response.setFee(txn.getFee());
        response.setTotalCharge(txn.getAmount().add(txn.getFee()));
        response.setPaymentMethod(txn.getPaymentMethod().getType());
        response.setStatus(txn.getStatus().name());
        response.setCreatedAt(txn.getCreatedAt());
        // Updated balances
        response.setSenderNewBalance(senderWallet.getBalance());
        response.setReceiverNewBalance(receiverWallet.getBalance());
        response.setDescription(txn.getDescription());

        return response;
    }

    // Helper method to get the current user's ID from SecurityContext
    private Long getCurrentUserId() {
        UserDetailsImpl userDetails =
            (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }
}
