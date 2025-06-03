package com.example.auth.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OtpCleanupScheduler {

    private final OtpService otpService;

    public OtpCleanupScheduler(OtpService otpService) {
        this.otpService = otpService;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000) // every hour
    public void cleanExpiredOtps() {
        otpService.deleteOtpsOlderThan2Hours();
    }
}
