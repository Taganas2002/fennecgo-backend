package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.DepositRequest;
import com.fennec.fennecgo.dto.request.TransactionRequest;
import com.fennec.fennecgo.dto.request.WithdrawalRequest;
import com.fennec.fennecgo.dto.response.TransactionResponse;
import com.fennec.fennecgo.exception.InsufficientBalanceException;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.*;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.repository.TransactionRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import com.fennec.fennecgo.services.Interface.FeeRuleService;
import com.fennec.fennecgo.services.Interface.TransactionService;
import com.fennec.fennecgo.dto.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final FeeRuleService feeRuleService;
    private final TransactionMapper transactionMapper;// for converting to/from DTO

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        // 1) Convert the request DTO to an entity
        Transaction transaction = transactionMapper.toEntity(request);

        // 2) Validate transaction type
        if (transaction.getTransactionType() == null) {
            throw new ResourceNotFoundException("Transaction type must be provided");
        }
        // Optionally re-fetch the transactionType from DB by ID if needed:
        // transaction.setTransactionType(transactionTypeRepository.findById(...))

        // 3) Check the transaction code (e.g., "DEPOSIT", "WITHDRAWAL", "BILL_PAYMENT", "TRANSFER")
        String code = transaction.getTransactionType().getCode().toUpperCase();

        // 4) Validate amount
        BigDecimal amount = transaction.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }

        // 5) Calculate fee
        BigDecimal fee = feeRuleService.calculateFee(transaction.getTransactionType(), amount);
        transaction.setFee(fee);

        // 6) Decide how to handle fromWallet / toWallet logic
        Wallet fromWallet = transaction.getFromWallet();
        Wallet toWallet = transaction.getToWallet();

        switch (code) {
            case "DEPOSIT":
                // For deposit, we might not have a "fromWallet" (or it could be a system wallet).
                // Typically user is "adding" money from an external source, so fromWallet can be null or system.
                if (fromWallet != null) {
                    // Possibly ignore or remove fromWallet, or throw an error if your design says it must be null
                    throw new IllegalArgumentException("fromWallet must be null for DEPOSIT");
                }
                if (toWallet == null) {
                    throw new ResourceNotFoundException("To wallet must be provided for DEPOSIT");
                }
                // No need to check fromWallet balance. We only "credit" toWallet with (amount).
                // But if your design uses a "system wallet", you'd do fromWallet=system, check system's balance, etc.
                break;

            case "WITHDRAWAL":
                // For withdrawal, user is removing money from fromWallet and possibly sending to a system wallet or null
                if (fromWallet == null) {
                    throw new ResourceNotFoundException("From wallet must be provided for WITHDRAWAL");
                }
                if (toWallet != null) {
                    // Possibly ignore or remove toWallet, or it might be the system wallet
                }
                // We must ensure fromWallet has enough balance for (amount + fee)
                checkBalanceAndDeduct(fromWallet, amount.add(fee));
                // Then no need to credit a user wallet. Possibly credit a "system" wallet if your design requires it
                break;

            case "BILL_PAYMENT":
                // For bill payment, fromWallet -> biller
                if (transaction.getBiller() == null) {
                    throw new IllegalArgumentException("Biller must be provided for BILL_PAYMENT transactions");
                }
                if (fromWallet == null) {
                    throw new ResourceNotFoundException("From wallet must be provided for BILL_PAYMENT");
                }
                // Possibly toWallet is null or some "biller wallet" if your design uses that
                checkBalanceAndDeduct(fromWallet, amount.add(fee));
                // No user toWallet credit. Possibly credit a "biller" wallet if you store that in DB
                break;

            case "TRANSFER":
                // For peer-to-peer or wallet-to-wallet transfer, fromWallet -> toWallet
                if (fromWallet == null) {
                    throw new ResourceNotFoundException("From wallet must be provided for TRANSFER");
                }
                if (toWallet == null) {
                    throw new ResourceNotFoundException("To wallet must be provided for TRANSFER");
                }
                checkBalanceAndDeduct(fromWallet, amount.add(fee));
                creditWallet(toWallet, amount);
                break;

            default:
                // If none of the above, handle or throw an error
                throw new IllegalArgumentException("Unsupported transaction type code: " + code);
        }

        // 7) If we haven't handled deposit logic yet (like DEPOSIT code),
        //    we do it outside the switch if needed:
        if ("DEPOSIT".equalsIgnoreCase(code)) {
            // Just credit toWallet
            creditWallet(toWallet, amount);
        }

        // 8) Mark transaction as SUCCESS or PENDING, depending on your flow
        // For deposit from external source, you might do PENDING first, then update to SUCCESS upon external confirmation
        transaction.setStatus(TransactionStatus.SUCCESS);

        // 9) Generate reference number
        transaction.setReferenceNumber(generateReferenceNumber());

        // 10) Save transaction
        transactionRepository.save(transaction);

        // 11) Return the response
        return transactionMapper.toResponse(transaction);
    }


    private void checkBalanceAndDeduct(Wallet wallet, BigDecimal totalNeeded) {
        if (wallet.getBalance().compareTo(totalNeeded) < 0) {
            throw new InsufficientBalanceException("Not enough balance in wallet id: " + wallet.getId());
        }
        wallet.setBalance(wallet.getBalance().subtract(totalNeeded));
        walletRepository.save(wallet);
    }

    private void creditWallet(Wallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    private String generateReferenceNumber() {
        return UUID.randomUUID().toString();
    }

    @Override
    public TransactionResponse deposit(DepositRequest request) {
        // 1) Validate deposit amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        // 2) Find wallet to credit
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + request.getWalletId()));

        // 3) Fetch the DEPOSIT transaction type (code = "DEPOSIT")
        TransactionType depositType = transactionTypeRepository.findByCode("DEPOSIT")
            .orElseThrow(() -> new ResourceNotFoundException("TransactionType with code 'DEPOSIT' not found"));

        // 4) Lookup PaymentMethod by ID
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
            .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found with id: " + request.getPaymentMethodId()));

        // 5) Create new deposit transaction with status PENDING
        Transaction transaction = new Transaction();
        transaction.setTransactionType(depositType);
        transaction.setFromWallet(null); // No originating wallet for deposit
        transaction.setToWallet(wallet);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setAmount(request.getAmount());

        // 6) Calculate fee using FeeRuleService (should return non-zero if rule matches)
        BigDecimal fee = feeRuleService.calculateFee(depositType, request.getAmount());
        transaction.setFee(fee);

        // 7) Set transaction status to PENDING
        transaction.setStatus(TransactionStatus.PENDING);


        // 8) Generate reference number based on payment method
        String refPrefix = paymentMethod.getType().toUpperCase() + "_";
        transaction.setReferenceNumber(refPrefix + UUID.randomUUID());

        // 9) Save transaction and update wallet balance if desired (or update later on confirmation)
        transactionRepository.save(transaction);
        // For deposits, you might delay wallet update until payment confirmation.
        // For simulation, we can compute expected balance:
        BigDecimal expectedNewBalance = wallet.getBalance().add(request.getAmount());
        BigDecimal totalCharge = request.getAmount().add(fee);

        // 10) Build and return the response
        TransactionResponse response = transactionMapper.toResponse(transaction);
        response.setNewBalance(expectedNewBalance);
        response.setTotalCharge(totalCharge);

        return response;
    }


    @Override
    public TransactionResponse withdraw(WithdrawalRequest request) {
        // Validate withdrawal amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        // Find wallet to debit
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + request.getWalletId()));

        // Fetch the WITHDRAWAL transaction type (assume code = "WITHDRAWAL")
        TransactionType withdrawalType = transactionTypeRepository.findByCode("WITHDRAWAL")
            .orElseThrow(() -> new ResourceNotFoundException("TransactionType with code 'WITHDRAWAL' not found"));

        // Look up the PaymentMethod by ID (if applicable, e.g., for bank withdrawal)
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
            .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found with id: " + request.getPaymentMethodId()));

        // Create new withdrawal transaction with status PENDING
        Transaction transaction = new Transaction();
        transaction.setTransactionType(withdrawalType);
        transaction.setFromWallet(wallet);
        transaction.setToWallet(null); // Typically, money is sent externally, so toWallet might be null or a system account
        transaction.setPaymentMethod(paymentMethod);
        transaction.setAmount(request.getAmount());
        
        // Calculate fee using FeeRuleService for withdrawal (if applicable)
        BigDecimal fee = feeRuleService.calculateFee(withdrawalType, request.getAmount());
        transaction.setFee(fee);
        
        // Check if wallet has enough balance (amount + fee)
        BigDecimal totalNeeded = request.getAmount().add(fee);
        if (wallet.getBalance().compareTo(totalNeeded) < 0) {
            throw new InsufficientBalanceException("Not enough balance in wallet id: " + wallet.getId());
        }
        
        // Deduct funds immediately for withdrawal (or you might hold funds until confirmation)
        wallet.setBalance(wallet.getBalance().subtract(totalNeeded));
        walletRepository.save(wallet);

        // Set transaction status to PENDING (will update to SUCCESS upon external confirmation)
        transaction.setStatus(TransactionStatus.PENDING);

        // Generate reference number based on payment method type
        String refPrefix = paymentMethod.getType().toUpperCase() + "_";
        transaction.setReferenceNumber(refPrefix + UUID.randomUUID());

        // Save transaction
        transactionRepository.save(transaction);

        // Build response (newBalance now reflects deducted amount)
        TransactionResponse response = transactionMapper.toResponse(transaction);
        response.setNewBalance(wallet.getBalance());
        response.setTotalCharge(totalNeeded);

        return response;
    }

    @Override
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return transactionMapper.toResponse(transaction);
    }
    
    
}
