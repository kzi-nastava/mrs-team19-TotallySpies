package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.AcceptanceState;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody UserLoginRequestDTO request) throws Exception{
        if (request.getPassword().equals("123") && request.getEmail().equals("milan.lazarevic@gmail.com")){
            UserLoginResponseDTO userLogin = new UserLoginResponseDTO();
            userLogin.setName("Milan");
            userLogin.setLastName("Lazarevic");
            return new ResponseEntity<UserLoginResponseDTO>(userLogin, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();

    }
    @PutMapping(value = "/update-password/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updatePassword(@RequestBody UpdateUserPasswordRequestDTO request, @PathVariable Long id) throws Exception{
        if(id == 1){
            return ResponseEntity.ok().build();
        }
        return  ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserRegisterResponseDTO> register(@Validated @RequestBody UserRegisterRequestDTO request) throws Exception{
        UserRegisterResponseDTO userRegister = new UserRegisterResponseDTO();
        userRegister.setId(1L);
        userRegister.setEmail(request.getEmail());
        userRegister.setName(request.getName());
        userRegister.setLastName(request.getLastName());
        userRegister.setPhoneNumber(request.getPhoneNumber());
        userRegister.setAddress(request.getAddress());
        userRegister.setProfilePicture(request.getProfilePicture());
        return new ResponseEntity<UserRegisterResponseDTO>(userRegister, HttpStatus.OK);
    }
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

}
