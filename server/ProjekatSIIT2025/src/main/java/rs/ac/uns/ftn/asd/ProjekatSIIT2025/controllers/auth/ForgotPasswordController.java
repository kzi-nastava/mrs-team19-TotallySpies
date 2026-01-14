package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.ChangedPasswordDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.MailBody;
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
    @PostMapping("/verify-email/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        //check if user with this email exists
        UserDetails user = userService.loadUserByUsername(email);

        //generate the otp
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        //form the mail
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request : " + otp)
                .subject("OTP for Forgot Password request")
                .build();
        Date expirationTime = new Date(System.currentTimeMillis() + 5 * 60 * 1000 );//expires in 5 minutes
        ForgotPassword forgotPassword = new ForgotPassword(otp,expirationTime, (User) user);
        //send the mail
        emailService.sendSimpleMessage(mailBody);
        //save the forgotPassword object
        forgotPasswordService.save(forgotPassword);

        return ResponseEntity.ok("Email sent for verification");

    }

    @PostMapping("/verify-otp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        //check if user with this email exists
        UserDetails user = userService.loadUserByUsername(email);

        //check if forgotPassword with that otp and user exists
        ForgotPassword forgotPassword = forgotPasswordService.findByOtpAndUser(otp, (User) user);

        //check if otp expired
        if(forgotPassword.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordService.deleteById(forgotPassword.getForgotPasswordId());
            return new ResponseEntity<>("OTP has expired", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("OTP verified!");
    }

    @PostMapping("/change-password/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangedPasswordDTO request,
                                                 @PathVariable String email){
        //if the password and reentered password do not match
        if(!Objects.equals(request.getPassword(), request.getRepeatedPassword())){
            return new ResponseEntity<>("Please enter the password again", HttpStatus.EXPECTATION_FAILED);
        }

        //if they match, encode the password and update it
        userService.updatePassword(email,request.getPassword());
        return ResponseEntity.ok("Password has been changed!");
    }
}
