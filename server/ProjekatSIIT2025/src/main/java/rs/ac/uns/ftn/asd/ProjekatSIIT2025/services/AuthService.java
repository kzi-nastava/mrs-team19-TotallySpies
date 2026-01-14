package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ActivationToken;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ActivationTokenRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtService;

    @Autowired
    EmailService emailService;

    @Autowired
    UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    ActivationTokenRepository activationTokenRepository;

    public UserTokenStateDTO verify(UserLoginRequestDTO request) {

        /*Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));*/
        //will throw DisabledException automatically if the account is not enabled.
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (DisabledException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account not activated. Check your email.");
        }
        String token = jwtService.generateToken(request.getEmail());
        Date expirationDate = jwtService.extractExpiration(token);
        UserTokenStateDTO userTokenState = new UserTokenStateDTO(token, expirationDate);
        return userTokenState;
    }


    public UserRegisterResponseDTO register(UserRegisterRequestDTO dto){
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        User user = new User(dto);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(UserRole.valueOf("PASSENGER"));
        user.setBlocked(false);
        user.setEnabled(false);
        userRepository.save(user);

        //send activation link
        String token = UUID.randomUUID().toString().replace("-", "");
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        activationTokenRepository.save(activationToken);

        String activationLink = "http://localhost:8080/api/v1/auth/activate?token=" + token;
        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .text("Click the link to activate (valid 24h): " + activationLink)
                .subject("Activate your account")
                .build();
        emailService.sendSimpleMessage(mailBody);

        return new UserRegisterResponseDTO(user);
    }
    @Transactional
    public void activateAccount(String token) {

        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token"));

        if (activationToken.isUsed())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already used");

        if (activationToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");

        User user = activationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        activationToken.setUsed(true);
        activationTokenRepository.save(activationToken);
    }

}
