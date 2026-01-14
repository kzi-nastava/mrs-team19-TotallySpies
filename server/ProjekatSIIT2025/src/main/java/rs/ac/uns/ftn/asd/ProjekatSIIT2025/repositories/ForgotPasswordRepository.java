package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ForgotPassword;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword,Integer> {
    @Query("select fp from ForgotPassword fp where fp.otp = ?1 and fp.user = ?2")
    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);
}
