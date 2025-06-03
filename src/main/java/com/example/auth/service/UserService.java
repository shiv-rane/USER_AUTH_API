package com.example.auth.service;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.model.OtpEntity;
import com.example.auth.model.User;
import com.example.auth.repository.OtpRepository;
import com.example.auth.repository.UserRepository;

import com.example.auth.security.JwtUtils;
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

    @Autowired
    private JwtUtils jwtUtils;

    public User registerUser(@NotNull RegisterRequest registerRequest){
        System.out.println("Received email: " + registerRequest.getEmail());

        if(!validation.isEmailValid(registerRequest.getEmail())){
            System.out.println("Validating email: [" + registerRequest.getEmail() + "]");
            throw new IllegalArgumentException("Invalid email");
        }
        if(!validation.isPassValid(registerRequest.getPassword())){
            throw new IllegalArgumentException("Password is weak");
        }
        if(userRepository.existsByEmail(registerRequest.getEmail())){
           throw new EntityNotFoundException("Email already exists");
        }

        User user = new User();
        user.setFirstname(registerRequest.getFirstname());
        user.setLastname(registerRequest.getLastname());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        return userRepository.save(user);
    }

    public boolean login(@NotNull LoginRequest loginRequest){

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
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

    public String verifyOtp(@NotNull String email, @NotNull  String otp){

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

        return jwtUtils.generateToken(email);
    }

//    public boolean resetPassword(@NotNull ResetPasswordDTO resetPasswordDTO){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        if(!resetPasswordDTO.equals(email)){
//            throw new IllegalArgumentException("Password cannot be reset");
//        }
//        User user = userRepository.findByEmail(resetPasswordDTO.getEmail());
//        if(user == null){
//            throw new EntityNotFoundException("Email doesn't exist");
//        }
//
//        String otp = otpService.generateOTP();
//        LocalDateTime expiryTime = otpService.calculateExpiryTime(5);
//        emailService.sendEmailforPassReset(resetPasswordDTO.getEmail(),otp);
//        OtpEntity otpEntity = new OtpEntity();
//        otpEntity.setEmail(resetPasswordDTO.getEmail());
//        otpEntity.setOtp(otp);
//        otpEntity.setExpiration_time(expiryTime);
//        otpRepository.save(otpEntity);
//
//        return true;
//    }
//
//    public void VerifyOTPForPassReset(String email, String otp){
//
//    }
    public void deleteUser(Integer id){
        if(userRepository.existsById(id)){
            userRepository.deleteById(id);
        }
        else{
            throw new EntityNotFoundException("User not found with id: " + id);
        }
    }
}
