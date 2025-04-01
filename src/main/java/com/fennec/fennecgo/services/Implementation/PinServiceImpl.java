package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.models.User;
import com.fennec.fennecgo.repository.UserRepository;
import com.fennec.fennecgo.services.Interface.PinService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PinServiceImpl implements PinService {

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
    }

    @Override
    public boolean verifyPin(User user, String rawPin) {
        boolean matches = passwordEncoder.matches(rawPin, user.getPin());
        if (matches) {
            user.setFailedPinAttempts(0);
            userRepository.save(user);
        }
        return matches;
    }

    @Override
    public int incrementFailedAttempts(User user) {
        int attempts = user.getFailedPinAttempts() + 1;
        user.setFailedPinAttempts(attempts);
        userRepository.save(user);
        return attempts;
    }

    @Override
    public boolean changePin(User user, String currentPin, String newPin) {
        if (passwordEncoder.matches(currentPin, user.getPin())) {
            user.setPin(passwordEncoder.encode(newPin));
            user.setFailedPinAttempts(0);
            userRepository.save(user);
            return true;
        } else {
            incrementFailedAttempts(user);
            return false;
        }
    }
}
