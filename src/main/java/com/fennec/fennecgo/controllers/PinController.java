package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.request.PinRequest;
import com.fennec.fennecgo.dto.request.ChangePinRequest;
import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.security.services.UserDetailsImpl;
import com.fennec.fennecgo.services.Interface.PinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/pin")
public class PinController {

    private final PinService pinService;
    private final UserRepository userRepository;

    public PinController(PinService pinService, UserRepository userRepository) {
        this.pinService = pinService;
        this.userRepository = userRepository;
    }

    // Helper: Retrieve the current authenticated User from the database.
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/set-pin")
    public ResponseEntity<?> setPin(@RequestBody PinRequest pinRequest) {
        User user = getCurrentUser();
        pinService.setPin(user, pinRequest.getPin());
        return ResponseEntity.ok("PIN set successfully");
    }

    @PostMapping("/verify-pin")
    public ResponseEntity<?> verifyPin(@RequestBody PinRequest pinRequest) {
        User user = getCurrentUser();
        if (user.getPin() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PIN is not set");
        }
        boolean valid = pinService.verifyPin(user, pinRequest.getPin());
        if (valid) {
            return ResponseEntity.ok("PIN verified");
        } else {
            int attempts = pinService.incrementFailedAttempts(user);
            if (attempts >= 3) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Too many failed attempts. Please log in again.");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid PIN");
        }
    }

    // Endpoint to change PIN: requires current PIN and new PIN.
    @PostMapping("/change-pin")
    public ResponseEntity<?> changePin(@RequestBody ChangePinRequest request) {
        User user = getCurrentUser();
        if (user.getPin() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PIN is not set. Please set a PIN first.");
        }
        boolean changed = pinService.changePin(user, request.getCurrentPin(), request.getNewPin());
        if (changed) {
            return ResponseEntity.ok("PIN changed successfully");
        } else {
            int attempts = user.getFailedPinAttempts(); // Updated in service method
            if (attempts >= 3) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Too many failed attempts. Please log in again.");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid current PIN");
        }
    }
}
