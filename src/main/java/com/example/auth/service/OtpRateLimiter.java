package com.example.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OtpRateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_VERIFY_ATTEMPTS = 5;
    private static final int MAX_OTP_REQUESTS = 3;

    public OtpRateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isVerificationBlocked(String email) {
        String key = "otp:verify:" + email;
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts != null && Integer.parseInt(attempts) >= MAX_VERIFY_ATTEMPTS;
    }

    public void recordFailedVerification(String email) {
        String key = "otp:verify:" + email;
        Long current = redisTemplate.opsForValue().increment(key);
        if (current == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(15));
        }
    }

    public void resetVerificationAttempts(String email) {
        redisTemplate.delete("otp:verify:" + email);
    }

    public boolean canRequestOtp(String email) {
        String key = "otp:requests:" + email;
        String requests = redisTemplate.opsForValue().get(key);
        return requests == null || Integer.parseInt(requests) < MAX_OTP_REQUESTS;
    }

    public void recordOtpRequest(String email) {
        String key = "otp:requests:" + email;
        Long current = redisTemplate.opsForValue().increment(key);
        if (current == 1) {
            redisTemplate.expire(key, Duration.ofHours(1)); // 3 requests per hour
        }
    }
}
