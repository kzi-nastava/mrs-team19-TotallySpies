package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RidePreviewResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.DriverService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.ProfileChangeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {
    @Autowired
    ProfileChangeService profileChangeService;

    @Autowired
    DriverService driverService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/profile-change-requests")
    public List<ProfileChangeRequestDTO> getPendingRequests(){
        return profileChangeService.getPendingRequests();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/profile-change-requests/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id){
        profileChangeService.approveRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/profile-change-requests/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id){
        profileChangeService.rejectRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-driver")
    public ResponseEntity<Void> createDriver(@RequestBody CreateDriverRequestDTO dto) {
        driverService.createDriver(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getAdminProfile(@PathVariable Long id) {
        UserProfileResponseDTO dto = new UserProfileResponseDTO();
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
        response.setEmail(request.getEmail());
        response.setName(request.getName());
        response.setLastName(request.getLastName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }*/

    @GetMapping(value = "/users/{id}",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArrayList<RidePreviewResponseDTO>> getAll(@PathVariable Long id){
        ArrayList<RidePreviewResponseDTO> rides = new ArrayList<>();

        RidePreviewResponseDTO ride1 = new RidePreviewResponseDTO();
        ride1.setId(1L);
        ride1.setStartTime(LocalDateTime.now().minusMinutes(30));
        ride1.setEndTime(LocalDateTime.now().minusMinutes(5));
        ride1.setTotalPrice(850.50);
        ride1.setRiderId(100L);
        ride1.setPassengers(new ArrayList<>(Arrays.asList(101L, 102L)));
        ride1.setPaths(new ArrayList<>(Arrays.asList(201L, 202L)));
        ride1.setRideRejectionId(null);
        ride1.setPanic(false);

        RidePreviewResponseDTO ride2 = new RidePreviewResponseDTO();
        ride2.setId(2L);
        ride2.setStartTime(LocalDateTime.now().minusHours(1));
        ride2.setEndTime(LocalDateTime.now().minusMinutes(40));
        ride2.setTotalPrice(1200.00);
        ride2.setRiderId(101L);
        ride2.setPassengers(new ArrayList<>(Arrays.asList(103L)));
        ride2.setPaths(new ArrayList<>(Arrays.asList(203L)));
        ride2.setRideRejectionId(300L);
        ride2.setPanic(false);

        RidePreviewResponseDTO ride3 = new RidePreviewResponseDTO();
        ride3.setId(3L);
        ride3.setStartTime(LocalDateTime.now().minusMinutes(15));
        ride3.setEndTime(null); // still ongoing
        ride3.setTotalPrice(450.75);
        ride3.setRiderId(102L);
        ride3.setPassengers(new ArrayList<>(Arrays.asList(104L, 105L, 106L)));
        ride3.setPaths(new ArrayList<>(Arrays.asList(204L, 205L, 206L)));
        ride3.setRideRejectionId(null);
        ride3.setPanic(true);

        rides.add(ride1);
        rides.add(ride2);
        rides.add(ride3);

        return new ResponseEntity<ArrayList<RidePreviewResponseDTO>>(rides, HttpStatus.OK);
    }

}