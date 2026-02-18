package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.users;

import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.ChangePasswordRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.PassengerRideHistoryResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.reports.ReportResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileUpdateRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.AuthService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.RideService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.UserService;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    RideService rideService;

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
    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping(value = "/history")
    public ResponseEntity<List<PassengerRideHistoryResponseDTO>> getPassengerRideHistory(
            @RequestParam( defaultValue = "startedAt") String sortBy,
            @RequestParam ( defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //tells spring how to parse string from request to localDateTime
            LocalDateTime from,   //for filtering by ride.createdAt
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to )

    {
        Set<String> allowedSorts = Set.of("startedAt", "finishedAt", "createdAt", "pickupAddress", "destinationAddress");
        if (!allowedSorts.contains(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort field");
        }
        Sort sort = null;

        if(sortDirection.equals("ASC")){
            sort = Sort.by(sortBy).ascending();
        }
        else{
            sort = Sort.by(sortBy).descending();
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PassengerRideHistoryResponseDTO> history = rideService.getPassengerHistory(email,sort, from, to);
        return new ResponseEntity<>(history, HttpStatus.OK);
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

            if (filename == null || filename.equals("null") || filename.contains("default-profile-image")) {
                path = Paths.get("src/main/java/rs/ac/uns/ftn/asd/ProjekatSIIT2025/resources/static/image/default-profile-image.jpg");
            } else {
                path = Paths.get("uploads/profile-images").resolve(filename).normalize();
            }

            Resource resource = new FileSystemResource(path.toFile());
            if (!resource.exists()) {
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

    @GetMapping(value = "/report")
    public ResponseEntity<ReportResponseDTO> getReport(@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                       @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                       @RequestParam(value = "targetEmail", required = false) String targetEmail,
                                                       Authentication auth){
        String email = auth.getName();
        ReportResponseDTO dto = userService.generateReport(email, from, to, targetEmail);
        return ResponseEntity.ok(dto);
    }
}