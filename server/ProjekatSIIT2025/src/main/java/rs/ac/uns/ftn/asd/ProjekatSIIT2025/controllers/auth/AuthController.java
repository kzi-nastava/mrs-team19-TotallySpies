package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.authentification.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.AcceptanceState;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.UserService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping(value = "/login")
    public String login(@RequestBody UserLoginRequestDTO request){
        return userService.verify(request);
    }
    /*@PutMapping(value = "/update-password/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePassword(@RequestBody UpdateUserPasswordRequestDTO request, @PathVariable Long id) throws Exception{
        if(id == 1){
            //update password
            return ResponseEntity.ok().build();
        }
        return  ResponseEntity.notFound().build();
    }*/

    @PostMapping(value = "/register")
    public ResponseEntity<UserRegisterResponseDTO> register(@RequestBody UserRegisterRequestDTO request) {
        UserRegisterResponseDTO response = userService.register(request);
        return new ResponseEntity<UserRegisterResponseDTO>(response, HttpStatus.CREATED);
    }
    }/*
    @GetMapping("/verify-email")
    public ResponseEntity<EmailVerificationResponseDTO> verifyEmail(@RequestParam("token") String token) {
        EmailVerificationResponseDTO response = new EmailVerificationResponseDTO();
        if (token.equals("123")){
            response.setMessage("Success");
            response.setStatus(AcceptanceState.ACCEPTED);
            return new ResponseEntity<EmailVerificationResponseDTO>( response, HttpStatus.CREATED);
        }else{
            response.setMessage("Error");
            response.setStatus(AcceptanceState.DECLINED);
            return new ResponseEntity<EmailVerificationResponseDTO>( response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}*/

