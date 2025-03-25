package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.WalletTransferRequest;
import com.fennec.fennecgo.dto.response.WalletTransferResponse;
import com.fennec.fennecgo.exception.InsufficientBalanceException;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.PaymentMethod;
import com.fennec.fennecgo.models.Transaction;
import com.fennec.fennecgo.models.TransactionStatus;
import com.fennec.fennecgo.models.TransactionType;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.models.Wallet;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.repository.TransactionRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.repository.WalletRepository;
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
    public WalletTransferResponse transferNow(WalletTransferRequest request) {
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
        
        // 3) Fetch receiver's default wallet using toUserId from the request
        User receiverUser = userRepository.findById(request.getToUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + request.getToUserId()));
        Wallet receiverWallet = walletRepository.findDefaultWalletByUserId(receiverUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Receiver's default wallet not found"));
        
        // 4) Validate the transfer amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }
        
        // 5) Check sender's wallet balance
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in sender's wallet");
        }
        
        // 6) Fetch transaction type "P2P_TRANSFER"
        TransactionType p2pType = transactionTypeRepository.findByCode("P2P_TRANSFER")
            .orElseThrow(() -> new ResourceNotFoundException("TransactionType 'P2P_TRANSFER' not found"));
        
        // 7) Fetch payment method "WALLET"
        PaymentMethod pm = paymentMethodRepository.findByTypeIgnoreCase("WALLET")
            .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod 'WALLET' not found"));
        
        // 8) Calculate fee (if applicable)
        BigDecimal fee = feeRuleService.calculateFee(p2pType, request.getAmount());
        
        // 9) Deduct funds from sender's wallet and credit receiver's wallet
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(senderWallet);
        
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));
        walletRepository.save(receiverWallet);
        
        // 10) Create a Transaction record with status SUCCESS
        Transaction txn = new Transaction();
        txn.setTransactionType(p2pType);
        txn.setFromWallet(senderWallet);
        txn.setToWallet(receiverWallet);
        txn.setPaymentMethod(pm);
        txn.setAmount(request.getAmount());
        txn.setFee(fee);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setDescription(request.getDescription()); // Include transfer note
        String refNumber = "WALLET_" + UUID.randomUUID();
        txn.setReferenceNumber(refNumber);
        transactionRepository.save(txn);
        
        // 11) Build the response with sender info
        WalletTransferResponse response = new WalletTransferResponse();
        response.setTransactionId(txn.getId());
        response.setReferenceNumber(refNumber);
        response.setAmount(request.getAmount());
        response.setFee(fee);
        response.setTotalCharge(request.getAmount().add(fee));
        response.setStatus(txn.getStatus().name());
        response.setPaymentMethod(pm.getType());
        response.setCreatedAt(txn.getCreatedAt());
        response.setSenderUsername(senderUser.getUsername());
        response.setSenderPhone(senderUser.getPhone());
        response.setSenderEmail(senderUser.getEmail());
        response.setDescription(request.getDescription());
        
        return response;
    }
    
    /**
     * Helper method to retrieve the current user's ID from the SecurityContext.
     */
    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }
}
