package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.users;

import jakarta.validation.Valid;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.ChangePasswordRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserImageUpdateDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileUpdateRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.AuthService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.UserService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

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

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO, Authentication auth) {
        String email = auth.getName();
        authService.changePassword(requestDTO, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        try {
            Path path;

            // 1. Provera da li je default slika
            if (filename == null || filename.equals("null") || filename.contains("default-profile-image")) {
                // Direktna putanja od korena projekta
                path = Paths.get("src/main/java/rs/ac/uns/ftn/asd/ProjekatSIIT2025/resources/static/image/default-profile-image.jpg");
            } else {
                // 2. Putanja za uploadovane slike
                path = Paths.get("uploads/profile-images").resolve(filename).normalize();
            }

            Resource resource = new FileSystemResource(path.toFile());

            // 3. Ako fajl ne postoji na prvoj lokaciji, proveri uploads kao fallback
            if (!resource.exists()) {
                // Zadnja linija odbrane: ako slika sa diska ne postoji, daj bilo šta što nađeš kao default
                path = Paths.get("src/main/java/rs/ac/uns/ftn/asd/ProjekatSIIT2025/resources/static/image/default-profile-image.jpg");
                resource = new FileSystemResource(path.toFile());
            }

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.IMAGE_JPEG))
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}