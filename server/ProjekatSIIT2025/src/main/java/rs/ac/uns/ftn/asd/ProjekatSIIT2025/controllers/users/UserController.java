package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.users;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserImageUpdateDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileUpdateRequestDTO;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable Long id) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(id);
        dto.setName("TestUser");
        dto.setLastName("UserLast");
        dto.setEmail("user@gmail.com");
        dto.setProfilePicture("default.png");
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUserProfile(@PathVariable Long id,
                                                  @RequestBody UserProfileUpdateRequestDTO request) {
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<Void> updateProfileImage(@PathVariable Long id,
                                                   @RequestBody UserImageUpdateDTO request) {
        return ResponseEntity.noContent().build();
    }
}