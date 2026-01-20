package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.users;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserImageUpdateDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileUpdateRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.UserService;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(Authentication auth) {
        String email = auth.getName();
        UserProfileResponseDTO response = userService.getProfileByEmail(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(@RequestBody @Valid UserProfileUpdateRequestDTO request, Authentication auth) {
        String email = auth.getName();
        userService.updateProfile(email, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/profile/image")
    public ResponseEntity<Void> updateProfileImage(@RequestBody @Valid UserImageUpdateDTO request, Authentication auth) {
        String email = auth.getName();
        userService.updateProfilePicture(email, request);
        return ResponseEntity.noContent().build();
    }
}