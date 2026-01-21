package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer forgotPasswordId;
    @Column(nullable = false)
    private Integer otp;
    @Column(nullable = false)
    private Date expirationTime;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ForgotPassword() {}

    public ForgotPassword(Integer otp, Date expirationTime, User user) {
        this.otp = otp;
        this.expirationTime = expirationTime;
        this.user = user;
    }

    public Integer getForgotPasswordId() {
        return forgotPasswordId;
    }

    public void setForgotPasswordId(Integer forgotPasswordId) {
        this.forgotPasswordId = forgotPasswordId;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
