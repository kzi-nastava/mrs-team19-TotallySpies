package com.ftn.mobile.data.remote.dto.auth;

public class VerifyOtpDTO {
    private String email;
    private Integer otp;

    public VerifyOtpDTO(String email, Integer otp) {
        this.email = email;
        this.otp = otp;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
