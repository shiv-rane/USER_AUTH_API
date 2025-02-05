package com.example.auth.controller;

import com.example.auth.model.LoginRequest;
import com.example.auth.model.User;
import com.example.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@RequestBody User user){
        return service.registerUser(user);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Integer id){
        service.deleteUser(id);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){
        boolean isAuthenticate = service.login(loginRequest);
        if(isAuthenticate){
            return ResponseEntity.ok("OTP sent to your registered email");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp){
        boolean verify = service.verifyOtp(email,otp);
        if(verify){
            return ResponseEntity.ok("Otp has been verified");
        }
        else{
            return ResponseEntity.status(400).body("Invalid Otp");
        }
    }
}
