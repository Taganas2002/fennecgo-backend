package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.response.WalletResponse;
import com.fennec.fennecgo.security.services.UserDetailsImpl;
import com.fennec.fennecgo.services.Interface.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    
 // Helper method to extract the current user's ID from the JWT
    private Long getCurrentUserId() {
        UserDetailsImpl userDetails =
            (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<WalletResponse> getWalletById(@PathVariable Long id) {
        WalletResponse response = walletService.getWalletById(id);
        return ResponseEntity.ok(response);
    }
    
    // Optionally, get wallet by user id
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<WalletResponse> getWalletByUserId(@PathVariable Long userId) {
        // Retrieve the wallet entity for the given user id
        WalletResponse response = walletService.getWalletById(
                walletService.getWalletEntityByUserId(userId).getId()
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getWalletForCurrentUser() {
        Long userId = getCurrentUserId();
        WalletResponse response = walletService.getWalletForCurrentUser(userId);
        return ResponseEntity.ok(response);
    }
}
