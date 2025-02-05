package com.example.auth.service;
import com.example.auth.model.LoginRequest;
import com.example.auth.model.OtpEntity;
import com.example.auth.model.User;
import com.example.auth.repository.OtpRepository;
import com.example.auth.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private Validation validation;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    public User registerUser(@NotNull User user){
        if(!validation.isEmailValid(user.getEmail())){
            throw new IllegalArgumentException("Invalid email");
        }
        if(!validation.isPassValid(user.getPassword())){
            throw new IllegalArgumentException("Password is weak");
        }
        if(userRepository.existsByEmail(user.getEmail())){
           throw new EntityNotFoundException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public boolean login(@NotNull LoginRequest loginRequest){

        User user = userRepository.findByEmail(loginRequest.getEmail());
       if(user == null){
           throw new EntityNotFoundException("Email doesn't exist");
       }

       boolean isPasswordValid = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
       if (!isPasswordValid) {
            throw new IllegalArgumentException("Invalid Credentials");
       }

        String otp = otpService.generateOTP();
        LocalDateTime expiryTime = otpService.calculateExpiryTime(5);

        emailService.sendEmail(loginRequest.getEmail(), otp);

        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setEmail(loginRequest.getEmail());
        otpEntity.setOtp(otp);
        otpEntity.setExpiration_time(expiryTime);
        otpRepository.save(otpEntity);

        return true;
    }

    public boolean verifyOtp(@NotNull String email, @NotNull  String otp){

        Optional<OtpEntity> otpEntityOpt = otpRepository.findByEmail(email);
        if(otpEntityOpt.isEmpty()){
            throw new EntityNotFoundException("Otp not found for this email");
        }

        OtpEntity otpEntity = otpEntityOpt.get();

        if(otpEntity.getExpiration_time().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Otp has expired");
        }

        if(!otpEntity.getOtp().equals(otp)){
            throw new IllegalThreadStateException("Invalid otp");
        }

        otpRepository.delete(otpEntity);

        return true;
    }

    public void resetPassword(String currentPass,String newPass){

    }

    public void deleteUser(Integer id){
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
        else{
            throw new EntityNotFoundException("User not found with id: " + id);
        }
    }
}
