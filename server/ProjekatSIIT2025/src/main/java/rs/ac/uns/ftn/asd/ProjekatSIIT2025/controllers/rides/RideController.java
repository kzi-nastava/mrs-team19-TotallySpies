package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.RideService;

@RestController
@RequestMapping("api/v1/rides")
public class RideController {

    @Autowired
    RideService rideService;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    RideRepository rideRepository;

    @GetMapping(value = "/{id}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideTrackingDTO> getRideLocation(@PathVariable Long id) {
        RideTrackingDTO response = rideService.getRideTrackingInfo(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/inconsistency-report", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Map<String, String>> reportInconsistency(
            @PathVariable Long id, 
            @Valid @RequestBody InconsistencyReportRequestDTO request) {
                boolean isReported = rideService.reportInconsistency(id, request);
                
                if (isReported) {
                    return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "Consistency report sent successfully."));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Failed to send consistency report."));
                }
    }

    @PutMapping(value = "/{id}/end", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideFinishResponseDTO> finishRide(@PathVariable Long id) {
        RideFinishResponseDTO response = rideService.finishRide(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/scheduled", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<RideFinishResponseDTO>> getFutureRides() {
        List<RideFinishResponseDTO> futureRides = rideService.findScheduledRides();
        return new ResponseEntity<>(futureRides, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/{rideId}/start")
    public ResponseEntity<String> startRide(@PathVariable Long rideId, Authentication auth) {
        String email = auth.getName();
        rideService.startRide(rideId, email);
        return ResponseEntity.ok("Ride successfully started!");
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/cancel-ride")
    public ResponseEntity<String> cancelRide(@RequestBody CancelRideDTO request){
        rideService.cancelRide(request);
        return ResponseEntity.ok("Ride cancelled!");
    }
    @PutMapping("/panic")
    public ResponseEntity<String> panic(@RequestBody PanicRideDTO request){
        rideService.handlePanicNotification(request);
        return ResponseEntity.ok("PANIC button activated!");
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/stop-ride")
    public ResponseEntity<String> stopRide(@RequestBody StopRideDTO request){
        rideService.stopRide(request);
        return ResponseEntity.ok("Ride stopped!");
    }


    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateRideResponseDTO> createRide(@Valid @RequestBody CreateRideRequestDTO requestDTO, Authentication auth){
        String email = auth.getName(); //email passengera, odnoson usera, koji je porucio voznju
        CreateRideResponseDTO responseDTO = rideService.createRide(requestDTO, email);
        return ResponseEntity.ok(responseDTO);
    }
    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping(value = "/{id}/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PassengerRideDetailsResponseDTO> getRideDetails(@PathVariable Long id ){
        PassengerRideDetailsResponseDTO response = rideService.getRideDetails(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}/details/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminRideDetailsResponseDTO> getRideDetailsForAdmin(@PathVariable Long id){
        AdminRideDetailsResponseDTO response = rideService.getRideDetailsForAdmin(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/active-admin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ActiveRideDTO>> getActiveRidesForAdmin(
        @RequestParam(required = false) String driverName) {
        
        List<ActiveRideDTO> activeRides = rideService.findActiveRides(driverName);
        return new ResponseEntity<>(activeRides, HttpStatus.OK);
    }

    @GetMapping(value = "/passenger-upcoming")
    public ResponseEntity<List<PassengerUpcomingRideDTO>> getUpcomingRidesForPassenger(Authentication auth){
        String email = auth.getName();
        List<PassengerUpcomingRideDTO> upcomingRides = rideService.findUpcomingRides(email);
        return ResponseEntity.ok(upcomingRides);
    }

    @GetMapping(value = "/active-tracking", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<RideTrackingDTO> getActiveRideTracking() {
        RideTrackingDTO response = rideService.getActiveRideForPassenger();
        
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/last-completed")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<RideTrackingDTO> getLastCompletedRide() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        RideTrackingDTO dto = rideService.getLastCompletedRideForPassenger(email);
        
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(dto);
    }

}
