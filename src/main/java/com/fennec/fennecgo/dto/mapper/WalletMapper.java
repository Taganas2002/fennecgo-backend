package com.fennec.fennecgo.dto.mapper;

import com.fennec.fennecgo.dto.request.WalletRequest;
import com.fennec.fennecgo.dto.response.WalletResponse;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.models.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class WalletMapper {

    /**
     * Converts a WalletRequest DTO to a Wallet entity.
     * 
     * @param request the WalletRequest coming from the client
     * @param user the associated User entity (determined from auth context or provided)
     * @return the Wallet entity ready for persistence
     */
    public Wallet toEntity(WalletRequest request, User user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setName(request.getName());
        // If no initial balance is provided, default to 0
        wallet.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO);
        // Default wallet type if not provided
        wallet.setWalletType(request.getWalletType() != null ? request.getWalletType() : "PERSONAL");
        // Set default status to ACTIVE
        wallet.setWalletStatus("ACTIVE");
        // Default daily limit can be set or 0 if not provided
        wallet.setDailyLimit(request.getDailyLimit() != null ? request.getDailyLimit() : BigDecimal.ZERO);
        // Aggregated totals start at 0
        wallet.setTotalDeposit(BigDecimal.ZERO);
        wallet.setTotalWithdrawal(BigDecimal.ZERO);
        // Set timestamps (if not handled automatically via @PrePersist in the entity)
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        return wallet;
    }
    
    public Wallet toDefaultWalletEntity(User user) {
    	Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setName("Default Wallet");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setWalletType("PERSONAL");
        wallet.setWalletStatus("ACTIVE");
        wallet.setDailyLimit(BigDecimal.valueOf(1000)); // e.g., default daily limit
        wallet.setTotalDeposit(BigDecimal.ZERO);
        wallet.setTotalWithdrawal(BigDecimal.ZERO);
		return wallet;
    }
    
    /**
     * Converts a Wallet entity to a WalletResponse DTO.
     * 
     * @param wallet the Wallet entity retrieved from the database
     * @return the WalletResponse DTO for returning to the client
     */
    public WalletResponse toResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setId(wallet.getId());
        response.setUserId(wallet.getUser().getId());
        response.setName(wallet.getName());
        response.setBalance(wallet.getBalance());
        response.setWalletType(wallet.getWalletType());
        response.setWalletStatus(wallet.getWalletStatus());
        response.setDailyLimit(wallet.getDailyLimit());
        response.setTotalDeposit(wallet.getTotalDeposit());
        response.setTotalWithdrawal(wallet.getTotalWithdrawal());
        response.setCreatedAt(wallet.getCreatedAt());
        response.setUpdatedAt(wallet.getUpdatedAt());
        return response;
    }
}
