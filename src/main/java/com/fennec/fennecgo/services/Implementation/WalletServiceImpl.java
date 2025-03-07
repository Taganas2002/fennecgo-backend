package com.fennec.fennecgo.services.Implementation;


import com.fennec.fennecgo.dto.mapper.WalletMapper;
import com.fennec.fennecgo.dto.response.WalletResponse;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.models.Wallet;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import com.fennec.fennecgo.services.Interface.WalletService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletServiceImpl.class);
    
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;

    /**
     * Creates a default wallet for a new user.
     */
    @Override
    public WalletResponse createDefaultWalletForUser(Long userId) {
        // Find the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Create default wallet
        Wallet wallet = walletMapper.toDefaultWalletEntity(user);

        Wallet savedWallet = walletRepository.save(wallet);
        LOGGER.info("Default wallet created for user id {} with wallet id {}", userId, savedWallet.getId());
        return walletMapper.toResponse(savedWallet);
    }

    @Override
    public WalletResponse getWalletById(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        return walletMapper.toResponse(wallet);
    }

    @Override
    public Wallet getWalletEntityByUserId(Long userId) {
        return walletRepository.findByUser_Id(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));
    }
    
    @Override
    public WalletResponse getWalletForCurrentUser(Long userId) {
        Wallet wallet = walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user id: " + userId));
        return walletMapper.toResponse(wallet);
    }
}
