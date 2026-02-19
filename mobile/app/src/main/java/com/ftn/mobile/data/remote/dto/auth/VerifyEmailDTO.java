package com.ftn.mobile.data.remote.dto.auth;

public class VerifyEmailDTO {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public VerifyEmailDTO(String email) {
        this.email = email;
    }
}
