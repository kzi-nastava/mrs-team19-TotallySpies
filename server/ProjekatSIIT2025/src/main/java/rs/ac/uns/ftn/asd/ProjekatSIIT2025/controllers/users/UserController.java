package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.users;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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


    @PutMapping(value = "/profile/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImage(@RequestParam("image") MultipartFile image, Authentication auth) {
        userService.updateProfileImage(auth.getName(), image);
        return ResponseEntity.noContent().build();
    }
}