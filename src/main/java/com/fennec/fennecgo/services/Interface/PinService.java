package com.fennec.fennecgo.services.Interface;

import com.fennec.fennecgo.models.User;

public interface PinService {
    void setPin(User user, String rawPin);
    boolean verifyPin(User user, String rawPin);
    int incrementFailedAttempts(User user);
    boolean changePin(User user, String currentPin, String newPin);

}
