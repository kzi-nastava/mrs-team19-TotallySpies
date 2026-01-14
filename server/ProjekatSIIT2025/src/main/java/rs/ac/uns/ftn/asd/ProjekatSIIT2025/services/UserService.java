package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;
import java.time.Instant;
import java.util.Date;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null){
            System.out.println("User with that email not found");
            throw new UsernameNotFoundException("User with that email not found");
        }
        return user;
    }

    public void updatePassword(String email, String password){
        String encodedPassword = encoder.encode(password);
        User user = userRepository.findByEmail(email);
        user.setPassword(encodedPassword);
        //userRepository.updatePassword(email, encodedPassword);
        //needed for jwt validation
        user.setLastPasswordResetDate(Date.from(Instant.now()));
        userRepository.save(user);
    }
    public UserRole getRoleByEmail(String email){
        return userRepository.getRoleByEmail(email);
    }

}
