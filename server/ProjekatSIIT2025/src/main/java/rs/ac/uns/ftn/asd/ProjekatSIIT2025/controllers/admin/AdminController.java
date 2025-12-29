package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverRegisterRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserImageUpdateDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileUpdateRequestDTO;

@RestController
@RequestMapping(value = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getAdminProfile(@PathVariable Long id) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
        dto.setId(id);
        dto.setName("Admin123");
        dto.setLastName("Administrator");
        dto.setEmail("admin@system.com");
        dto.setProfilePicture("admin.png");

        return ResponseEntity.ok(dto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateAdminProfile(
            @PathVariable Long id,
            @RequestBody UserProfileUpdateRequestDTO request) {

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateAdminImage(
            @PathVariable Long id,
            @RequestBody UserImageUpdateDTO request) {

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/drivers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfileResponseDTO> createDriver(@RequestBody DriverRegisterRequestDTO request) {
        UserProfileResponseDTO response = new UserProfileResponseDTO();
        response.setId(55L);
        response.setEmail(request.getEmail());
        response.setName(request.getName());
        response.setLastName(request.getLastName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}