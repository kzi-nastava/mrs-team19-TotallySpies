package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ActivationToken;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ImageMetaData;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ActivationTokenRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;

import java.io.IOException;
import java.time.Instant;
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
    FileStorageService imageService;

    @Autowired
    UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    ActivationTokenRepository activationTokenRepository;

    public UserTokenStateDTO verify(UserLoginRequestDTO request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        }catch (BadCredentialsException | UsernameNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        catch (DisabledException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account not activated. Check your email.");
        }

        User user = userRepository.findByEmail(request.getEmail());
        String token = jwtService.generateToken(user);
        Date expirationDate = jwtService.extractExpiration(token);
        UserTokenStateDTO userTokenState = new UserTokenStateDTO(token, expirationDate);
        return userTokenState;
    }
    public void register(UserRegisterRequestDTO dto){
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (!(dto.getPassword().equals(dto.getConfirmedPassword()))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password and confirmed password need to match!");
        }
        User user = new User(dto);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(UserRole.valueOf("PASSENGER"));
        user.setBlocked(false);
        user.setEnabled(false);
        user = userRepository.save(user);

        //handle profile picture upload
        MultipartFile profilePicture = dto.getProfilePicture();
        if (profilePicture != null && !profilePicture.isEmpty()){
            //ImageMetaData metaData = new ImageMetaData();
            //metaData.setContentType("users"); //folder type
           // metaData.setContentId(user.getId());
            String storedName = imageService.storeProfileImage(profilePicture, user.getId());
            //user.setProfilePicture("images/users/" + user.getId() + "/" + storedName);
            user.setProfilePicture(storedName);
            userRepository.save(user);
        }

        //send activation link
        String token = UUID.randomUUID().toString().replace("-", "");
        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        activationTokenRepository.save(activationToken);
        String activationLink = "http://localhost:4200/activate-account?token=" + token;
        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .text("Click the link to activate (valid 24h): " + activationLink)
                .subject("Activate your account")
                .build();
        emailService.sendSimpleMessage(mailBody);
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

    @Transactional
    public void activateDriver(DriverActivationRequestDTO dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        ActivationToken token = activationTokenRepository
                .findByToken(dto.getToken())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid token"));

        if (token.isUsed())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token already used");

        if (token.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");

        User user = token.getUser();

        String encodedPassword = encoder.encode(dto.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        userRepository.save(user);

        token.setUsed(true);
        activationTokenRepository.save(token);
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDTO dto, String email) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        if (!encoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (encoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from old password");
        }

        String encoded = encoder.encode(dto.getNewPassword());
        user.setPassword(encoded);
        user.setLastPasswordResetDate(Date.from(Instant.now()));

        userRepository.save(user);
    }
}
