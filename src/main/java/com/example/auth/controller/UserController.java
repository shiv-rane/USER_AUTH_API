package com.example.auth.controller;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.OtpVerificationDTO;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.dto.ResetPasswordDTO;
import com.example.auth.model.User;
import com.example.auth.service.OtpRateLimiter;
import com.example.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private OtpRateLimiter otpRateLimiter;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody RegisterRequest request){
        return service.registerUser(request);
    }

    @GetMapping("/me")
    public String hello(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return email;
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Integer id){
        service.deleteUser(id);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){

        if (!otpRateLimiter.canRequestOtp(loginRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many OTP requests. Try again later.");
        }

        boolean isAuthenticate = service.login(loginRequest);

        if(isAuthenticate){
            otpRateLimiter.recordOtpRequest(loginRequest.getEmail());
            return ResponseEntity.ok("OTP sent to your registered email");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationDTO otpVerificationDTO){
        Map<String , String> response = new HashMap<>();

        if (otpRateLimiter.isVerificationBlocked(otpVerificationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Too many failed attempts. Try after some time.");
        }

        String token = service.verifyOtp(otpVerificationDTO.getEmail(), otpVerificationDTO.getOtp());
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(3600)
                .sameSite("Strict")
                .build();
        response.put("message", "OTP verified successfully");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO resetpasswordDTO){
//        boolean reset = service.resetPassword(resetpasswordDTO);
//        return ResponseEntity.ok("OTP sent to your email");
//    }
}
