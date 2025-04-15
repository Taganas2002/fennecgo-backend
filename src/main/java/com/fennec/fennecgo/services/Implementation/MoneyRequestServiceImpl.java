package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.mapper.MoneyRequestMapper;
import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import com.fennec.fennecgo.exception.InsufficientBalanceException;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.MoneyRequest;
import com.fennec.fennecgo.models.PaymentMethod;
import com.fennec.fennecgo.models.Transaction;
import com.fennec.fennecgo.models.TransactionStatus;
import com.fennec.fennecgo.models.TransactionType;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.models.Wallet;
import com.fennec.fennecgo.repository.MoneyRequestRepository;
import com.fennec.fennecgo.repository.TransactionRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.security.services.UserDetailsImpl;
import com.fennec.fennecgo.services.Interface.MoneyRequestService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MoneyRequestServiceImpl implements MoneyRequestService {

    private static final Logger log = LoggerFactory.getLogger(MoneyRequestServiceImpl.class);

    private final MoneyRequestRepository moneyRequestRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final MoneyRequestMapper moneyRequestMapper;

    @Override
    public MoneyRequestResponse createMoneyRequest(MoneyRequestRequest request) {
        Long requestorUserId = getCurrentUserId();
        User requestorUser = userRepository.findById(requestorUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Requestor not found with id: " + requestorUserId));
        Wallet requestorWallet = walletRepository.findDefaultWalletByUserId(requestorUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Requestor's default wallet not found"));

        User payerUser = userRepository.findById(request.getPayerUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Payer not found with id: " + request.getPayerUserId()));
        Wallet payerWallet = walletRepository.findDefaultWalletByUserId(payerUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Payer's default wallet not found"));

        MoneyRequest moneyRequest = moneyRequestMapper.toMoneyRequest(request);
        moneyRequest.setFromWalletID(payerWallet);
        moneyRequest.setToWalletID(requestorWallet);
        moneyRequest.setStatus("PENDING");
        moneyRequest.setReferenceNumber("MR_" + UUID.randomUUID());

        MoneyRequest savedRequest = moneyRequestRepository.save(moneyRequest);
        log.info("Created MoneyRequest with reference {}", savedRequest.getReferenceNumber());
        return moneyRequestMapper.toMoneyRequestResponse(savedRequest);
    }

    @Override
    public MoneyRequestResponse getMoneyRequestById(Long id) {
        MoneyRequest moneyRequest = moneyRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("MoneyRequest not found with id " + id));
        return moneyRequestMapper.toMoneyRequestResponse(moneyRequest);
    }

    @Override
    public List<MoneyRequestResponse> getRequestsForUser() {
        Long currentUserId = getCurrentUserId();
        List<Wallet> wallets = walletRepository.findByUserId(currentUserId);
        List<Long> walletIds = wallets.stream().map(Wallet::getId).collect(Collectors.toList());
        List<MoneyRequest> requests = moneyRequestRepository.findAll().stream()
            .filter(req -> walletIds.contains(req.getFromWalletID().getId()) ||
                           walletIds.contains(req.getToWalletID().getId()))
            .collect(Collectors.toList());
        return requests.stream()
                .map(moneyRequestMapper::toMoneyRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MoneyRequestResponse> getRequestsForPayer() {
        Long currentUserId = getCurrentUserId();
        List<Wallet> wallets = walletRepository.findByUserId(currentUserId);
        List<Long> walletIds = wallets.stream().map(Wallet::getId).collect(Collectors.toList());
        List<MoneyRequest> requests = moneyRequestRepository.findByFromWalletID_IdIn(walletIds);
        return requests.stream()
                .map(moneyRequestMapper::toMoneyRequestResponse)
                .collect(Collectors.toList());
    }

    // Updated cancel method to set status to "CANCELED" and return the updated response.
    @Override
    public MoneyRequestResponse cancelMoneyRequest(String referenceNumber) {
        MoneyRequest moneyRequest = moneyRequestRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("MoneyRequest not found with reference " + referenceNumber));
        Long currentUserId = getCurrentUserId();
        if (!moneyRequest.getToWalletID().getUser().getId().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to cancel this money request");
        }
        if (!"PENDING".equalsIgnoreCase(moneyRequest.getStatus())) {
            throw new IllegalStateException("Only PENDING requests can be cancelled");
        }
        moneyRequest.setStatus("CANCELED");
        MoneyRequest updatedRequest = moneyRequestRepository.save(moneyRequest);
        log.info("Money request {} cancelled by user {}", referenceNumber, currentUserId);
        return moneyRequestMapper.toMoneyRequestResponse(updatedRequest);
    }

    @Override
    public MoneyRequestResponse confirmMoneyRequest(String referenceNumber) {
        MoneyRequest moneyRequest = moneyRequestRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("MoneyRequest not found with reference " + referenceNumber));
        if (!"PENDING".equalsIgnoreCase(moneyRequest.getStatus())) {
            throw new IllegalStateException("Only PENDING requests can be confirmed");
        }
        Long currentUserId = getCurrentUserId();
        if (!moneyRequest.getFromWalletID().getUser().getId().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to confirm this money request");
        }

        Wallet payerWallet = moneyRequest.getFromWalletID();
        Wallet requestorWallet = moneyRequest.getToWalletID();
        BigDecimal amount = moneyRequest.getAmount();

        if (payerWallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Payer does not have sufficient funds");
        }

        payerWallet.setBalance(payerWallet.getBalance().subtract(amount));
        walletRepository.save(payerWallet);

        requestorWallet.setBalance(requestorWallet.getBalance().add(amount));
        walletRepository.save(requestorWallet);

        moneyRequest.setStatus("PAID");
        moneyRequestRepository.save(moneyRequest);

        TransactionType auditType = transactionTypeRepository.findByCode("MONEY_REQUEST_TRANSFER")
        	    .orElseThrow(() -> new ResourceNotFoundException("TransactionType 'MONEY_REQUEST_TRANSFER' not found"));
        PaymentMethod pm = paymentMethodRepository.findByTypeIgnoreCase("WALLET")
            .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod 'WALLET' not found"));

        Transaction auditTransaction = new Transaction();
        auditTransaction.setTransactionType(auditType);
        auditTransaction.setFromWallet(payerWallet);
        auditTransaction.setToWallet(requestorWallet);
        auditTransaction.setPaymentMethod(pm);
        auditTransaction.setAmount(amount);
        auditTransaction.setFee(BigDecimal.ZERO);
        auditTransaction.setStatus(TransactionStatus.SUCCESS);
        auditTransaction.setReferenceNumber(moneyRequest.getReferenceNumber());
        auditTransaction.setDescription("Audit record for Money Request: " + moneyRequest.getReferenceNumber());
        transactionRepository.save(auditTransaction);

        log.info("Money request {} confirmed by user {}", referenceNumber, currentUserId);
        return moneyRequestMapper.toMoneyRequestResponse(moneyRequest);
    }

    @Override
    public MoneyRequestResponse declineMoneyRequest(String referenceNumber) {
        MoneyRequest moneyRequest = moneyRequestRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("MoneyRequest not found with reference " + referenceNumber));
        if (!"PENDING".equalsIgnoreCase(moneyRequest.getStatus())) {
            throw new IllegalStateException("Only PENDING requests can be declined");
        }
        Long currentUserId = getCurrentUserId();
        if (!moneyRequest.getFromWalletID().getUser().getId().equals(currentUserId)) {
            throw new SecurityException("You are not authorized to decline this money request");
        }
        moneyRequest.setStatus("DECLINED");
        MoneyRequest updatedRequest = moneyRequestRepository.save(moneyRequest);
        log.info("Money request {} declined by user {}", referenceNumber, currentUserId);
        return moneyRequestMapper.toMoneyRequestResponse(updatedRequest);
    }

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }
}
