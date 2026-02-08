package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.auth;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.AuthService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.DriverService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.UserService;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Autowired
    UserService userService;

    @Autowired
    DriverService driverService;

    @Autowired
    AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserTokenStateDTO> login(@Valid @RequestBody UserLoginRequestDTO request) {
        UserTokenStateDTO userTokenStateDTO = authService.verify(request);
         //if logged user is a driver, he becomes active
        if (userService.getRoleByEmail(request.getEmail()) == UserRole.DRIVER){
            driverService.setActiveDriver(request.getEmail());
        }
        // return token for successful authentication
        //runs only if no exception was thrown from service
        return ResponseEntity.ok(userTokenStateDTO);
    }

    @PostMapping(value = "/register",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> register(@Valid @ModelAttribute UserRegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.ok("User registered!");
    }

    @PostMapping(value = "/activate")
    public ResponseEntity<Void> activateAccount(@RequestParam String token) {
        authService.activateAccount(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/activate-driver")
    public ResponseEntity<Void> activateDriverAccount(@RequestBody DriverActivationRequestDTO requestDTO){
        authService.activateDriver(requestDTO);
        return ResponseEntity.ok().build();
    }

}

