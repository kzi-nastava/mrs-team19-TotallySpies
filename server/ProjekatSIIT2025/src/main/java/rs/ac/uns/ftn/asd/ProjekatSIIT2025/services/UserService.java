package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.authentification.UserLoginRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.authentification.UserLoginResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.authentification.UserRegisterRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.authentification.UserRegisterResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserRegisterResponseDTO register(UserRegisterRequestDTO dto){
        User user = new User(dto);
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return new UserRegisterResponseDTO(user);
    }

    public String verify(UserLoginRequestDTO request){
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if (authentication.isAuthenticated()){
            return jwtService.generateToken(request.getEmail());
        }
        return "Failed";
    }
}
