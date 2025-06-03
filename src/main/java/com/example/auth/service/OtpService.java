package com.example.auth.service;

import com.example.auth.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    public String generateOTP() {
        final SecureRandom random = new SecureRandom();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public LocalDateTime calculateExpiryTime(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    public boolean isOtpValid(String enteredOtp, String storedOtp, LocalDateTime expirationTime) {
        if (LocalDateTime.now().isAfter(expirationTime)) {
            return false; // OTP expired
        }
        return storedOtp.equals(enteredOtp); // Match OTP
    }

    public void deleteOtpsOlderThan2Hours() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(2);
        otpRepository.deleteExpiredOtps(cutoff);
    }

}
