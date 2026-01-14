package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ForgotPassword;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ForgotPasswordRepository;

import java.util.Optional;

@Service
public class ForgotPasswordService {
    @Autowired
    ForgotPasswordRepository forgotPasswordRepository;


    public void save(ForgotPassword forgotPassword){
        forgotPasswordRepository.save(forgotPassword);
    }
    public ForgotPassword findByOtpAndUser(Integer otp, User user) {
        return forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + user.getEmail()));
    }
    public void deleteById(Integer id){
        forgotPasswordRepository.deleteById(id);
    }
}
