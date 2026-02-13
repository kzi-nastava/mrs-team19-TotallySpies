package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.AdminRideHistoryResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.PanicNotificationDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.PassengerRideHistoryResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RidePreviewResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.PanicNotification;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {
    @Autowired
    ProfileChangeService profileChangeService;

    @Autowired
    DriverService driverService;

    @Autowired
    PanicNotificationService panicNotificationService;

    @Autowired
    UserService userService;

    @Autowired
    RideService rideService;

    @Autowired
    UserRepository userRepository;

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

    @GetMapping("/panic-notifications")
    public ResponseEntity<List<PanicNotification>> getPanicNotifications() {
        List<PanicNotification> panicNotifications =
                panicNotificationService.getPanicNotifications();

        return ResponseEntity.ok(panicNotifications);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/drivers")
    public List<AdminUserDTO> getDrivers() {
        return userService.findAllUsersByRole(UserRole.DRIVER);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/passengers")
    public List<AdminUserDTO> getPassengers() {
        return userService.findAllUsersByRole(UserRole.PASSENGER);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/block/{id}")
    public void blockUser(@PathVariable Long id, @RequestBody BlockRequestDTO req) {
        userService.blockUser(id, req.getReason());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/unblock/{id}")
    public void unblockUser(@PathVariable Long id) {
        userService.unblockUser(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{userId}/history")
    public ResponseEntity<List<AdminRideHistoryResponseDTO>> getAdminRideHistory(
            @PathVariable Long userId,
            @RequestParam int userIndicator, //1 is driver , 2 is passenger
            @RequestParam( defaultValue = "startedAt") String sortBy,
            @RequestParam ( defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //tells spring how to parse string from request to localDateTime
            LocalDateTime from,   //for filtering by ride.createdAt
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to )

    {
        Set<String> allowedSorts = Set.of("startedAt", "finishedAt", "createdAt", "pickupAddress",
                "destinationAddress", "totalPrice", "userWhoCancelled", "isCancelled", "isPanic");
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
        List<AdminRideHistoryResponseDTO> history = rideService.getAdminHistory(userId, userIndicator,
                sort, from, to);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

}