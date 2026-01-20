package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserImageUpdateDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileUpdateRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ProfileField;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;
import java.time.Instant;
import java.util.Date;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileChangeService profileChangeService;

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

    public UserProfileResponseDTO getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setProfilePicture(user.getProfilePicture() != null ? user.getProfilePicture() : "default.png");

        return dto;
    }

    public void updateProfile(String email, UserProfileUpdateRequestDTO request){
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (user.getRole() == UserRole.DRIVER) {
            updateProfileDriver(user, request);
        } else {
            if (request.getName() != null){
                user.setName(request.getName());
            }
            if (request.getLastName() != null){
                user.setLastName(request.getLastName());
            }
            if (request.getPhoneNumber() != null) {
                user.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getAddress() != null) {
                user.setAddress(request.getAddress());
            }
            userRepository.save(user);
        }
    }

    public void updateProfileDriver(User user, UserProfileUpdateRequestDTO requestDTO){
        if (requestDTO.getName() != null){
            profileChangeService.createChangeRequest(
                    user,
                    ProfileField.NAME,
                    user.getName(),
                    requestDTO.getName()
            );
        }
        if (requestDTO.getLastName() != null){
            profileChangeService.createChangeRequest(
                    user,
                    ProfileField.LAST_NAME,
                    user.getName(),
                    requestDTO.getName()
            );
        }
        if (requestDTO.getPhoneNumber() != null) {
            profileChangeService.createChangeRequest(
                    user,
                    ProfileField.PHONE,
                    user.getName(),
                    requestDTO.getName()
            );
        }
        if (requestDTO.getAddress() != null) {
            profileChangeService.createChangeRequest(
                    user,
                    ProfileField.ADDRESS,
                    user.getName(),
                    requestDTO.getName()
            );
        }
    }

    public void updateProfilePicture(String email, UserImageUpdateDTO request){
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        user.setProfilePicture(request.getImageUrl());
        userRepository.save(user);
    }
}
