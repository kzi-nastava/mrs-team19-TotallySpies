package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth;

public class VerifyOtpDTO {
    private String email;
    private Integer otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }
}
