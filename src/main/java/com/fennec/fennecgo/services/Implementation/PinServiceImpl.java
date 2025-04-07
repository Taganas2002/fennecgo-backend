package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.services.Interface.PinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PinServiceImpl implements PinService {

    private static final Logger logger = LoggerFactory.getLogger(PinServiceImpl.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PinServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void setPin(User user, String rawPin) {
        user.setPin(passwordEncoder.encode(rawPin));
        user.setFailedPinAttempts(0);
        userRepository.save(user);
        logger.info("PIN set for user with ID: {}", user.getId());
    }

    @Override
    public boolean verifyPin(User user, String rawPin) {
        boolean matches = passwordEncoder.matches(rawPin, user.getPin());
        if (matches) {
            user.setFailedPinAttempts(0);
            userRepository.save(user);
            logger.info("User with ID: {} successfully verified PIN", user.getId());
        } else {
            logger.warn("Failed PIN verification attempt for user with ID: {}", user.getId());
        }
        return matches;
    }

    @Override
    public int incrementFailedAttempts(User user) {
        int attempts = user.getFailedPinAttempts() + 1;
        user.setFailedPinAttempts(attempts);
        userRepository.save(user);
        logger.warn("Incremented failed PIN attempts for user with ID: {} to {}", user.getId(), attempts);
        return attempts;
    }

    @Override
    public boolean changePin(User user, String currentPin, String newPin) {
        if (passwordEncoder.matches(currentPin, user.getPin())) {
            user.setPin(passwordEncoder.encode(newPin));
            user.setFailedPinAttempts(0);
            userRepository.save(user);
            logger.info("User with ID: {} successfully changed PIN", user.getId());
            return true;
        } else {
            int attempts = incrementFailedAttempts(user);
            logger.warn("User with ID: {} failed to change PIN. Current failed attempt count: {}", user.getId(), attempts);
            return false;
        }
    }
}
