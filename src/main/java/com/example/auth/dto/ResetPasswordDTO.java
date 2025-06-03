package com.example.auth.dto;

import jakarta.validation.constraints.Email;

public class ResetPasswordDTO {

    @Email
    String email;

    String new_pass;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNew_pass() {
        return new_pass;
    }

    public void setNew_pass(String new_pass) {
        this.new_pass = new_pass;
    }
}
