package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.ChangedPasswordDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.MailBody;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.VerifyEmailDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.VerifyOtpDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ForgotPassword;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.EmailService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.ForgotPasswordService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.UserService;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/forgot-password")
public class ForgotPasswordController {

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    ForgotPasswordService forgotPasswordService;

    //send email for verification
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailDTO request){
        //check if user with this email exists
        try{
            UserDetails user = userService.loadUserByUsername(request.getEmail());
            //generate the otp
            SecureRandom random = new SecureRandom();
            int otp = 100000 + random.nextInt(900000);
            //form the mail
            MailBody mailBody = MailBody.builder()
                    .to(request.getEmail())
                    .text("This is the OTP for your Forgot Password request : " + otp)
                    .subject("OTP for Forgot Password request")
                    .build();
            Date expirationTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000 );//expires in 5 minutes
            ForgotPassword forgotPassword = new ForgotPassword(otp,expirationTime, (User) user);
            //send the mail
            emailService.sendSimpleMessage(mailBody);
            //save the forgotPassword object
            forgotPasswordService.save(forgotPassword);
            return ResponseEntity.ok("Email sent for verification");
        }
        catch(UsernameNotFoundException ex){
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email does not exists.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found!");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpDTO request){
        //check if user with this email exists
        UserDetails user;
        try {
            user = userService.loadUserByUsername(request.getEmail());
        }
        catch(UsernameNotFoundException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found!");
        }
        ForgotPassword forgotPassword = forgotPasswordService.findByOtpAndUser(request.getOtp(), (User) user);
        if(forgotPassword == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
        }
        //check if otp expired
        if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordService.deleteById(forgotPassword.getForgotPasswordId());
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "OTP has expired");
        }
        return ResponseEntity.ok("OTP verified!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangedPasswordDTO request){
        //if the password and reentered password do not match
        if(!Objects.equals(request.getPassword(), request.getRepeatedPassword())){
            //return new ResponseEntity<>("Please enter the password again", HttpStatus.EXPECTATION_FAILED);
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Please enter the password again");
        }
        //if they match, encode the password and update it
        userService.updatePassword(request.getEmail(),request.getPassword());
        return ResponseEntity.ok("Password has been changed!");
    }
}
