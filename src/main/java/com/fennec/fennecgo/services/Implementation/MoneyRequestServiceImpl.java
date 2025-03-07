package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.mapper.MoneyRequestMapper;
import com.fennec.fennecgo.dto.request.MoneyRequestRequest;
import com.fennec.fennecgo.dto.response.MoneyRequestResponse;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.MoneyRequest;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.models.Wallet;
import com.fennec.fennecgo.repository.MoneyRequestRepository;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import com.fennec.fennecgo.services.Interface.MoneyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoneyRequestServiceImpl implements MoneyRequestService {

    private final MoneyRequestRepository moneyRequestRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final MoneyRequestMapper moneyRequestMapper;

    @Override
    public MoneyRequestResponse createMoneyRequest(Long requestorUserId, MoneyRequestRequest request) {
        // 1. Find requestor's default wallet (the wallet to receive funds)
        Wallet requestorWallet = walletRepository.findDefaultWalletByUserId(requestorUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Requestor's default wallet not found"));

        // 2. Find the payer's user and their default wallet
        User payerUser = userRepository.findById(request.getPayerUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getPayerUserId()));
        Wallet payerWallet = walletRepository.findDefaultWalletByUserId(payerUser.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Payer's default wallet not found"));

        // 3. Build the MoneyRequest entity using the mapper helper
        MoneyRequest moneyRequest = moneyRequestMapper.toMoneyRequest(request, payerWallet.getId(), requestorWallet.getId());
        // Set the associations by assigning the fetched Wallet entities
        moneyRequest.setFromWalletID(payerWallet);
        moneyRequest.setToWalletID(requestorWallet);

        MoneyRequest savedRequest = moneyRequestRepository.save(moneyRequest);
        return moneyRequestMapper.toMoneyRequestResponse(savedRequest);
    }

    @Override
    public MoneyRequestResponse getMoneyRequestById(Long id) {
        MoneyRequest moneyRequest = moneyRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("MoneyRequest not found with id " + id));
        return moneyRequestMapper.toMoneyRequestResponse(moneyRequest);
    }

    @Override
    public List<MoneyRequestResponse> getRequestsForUser(Long userId) {
        // Get all wallets for the user
        List<Wallet> wallets = walletRepository.findByUserId(userId);
        List<Long> walletIds = wallets.stream().map(Wallet::getId).collect(Collectors.toList());

        // Fetch requests where either fromWallet or toWallet belongs to these wallet IDs
        List<MoneyRequest> requests = moneyRequestRepository.findAll()
            .stream()
            .filter(req -> walletIds.contains(req.getFromWalletID().getId()) 
                        || walletIds.contains(req.getToWalletID().getId()))
            .collect(Collectors.toList());

        return requests.stream()
                .map(moneyRequestMapper::toMoneyRequestResponse)
                .collect(Collectors.toList());
    }
    
    

    @Override
    public void cancelMoneyRequest(Long requestId) {
        MoneyRequest moneyRequest = moneyRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("MoneyRequest not found with id " + requestId));
        moneyRequest.setStatus("CANCELLED");
        moneyRequestRepository.save(moneyRequest);
    }

    public List<MoneyRequestResponse> getRequestsForPayer(Long userId) {
        // Get the user's wallet IDs
        List<Long> walletIds = walletRepository.findByUserId(userId)
            .stream()
            .map(Wallet::getId)
            .collect(Collectors.toList());

        // Fetch only the money requests where fromWalletID is in that list
        List<MoneyRequest> requests = moneyRequestRepository.findByFromWalletID_IdIn(walletIds);

        // Map to DTOs
        return requests.stream()
            .map(moneyRequestMapper::toMoneyRequestResponse)
            .collect(Collectors.toList());
    }

}
