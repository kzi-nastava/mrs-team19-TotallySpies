package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.RideService;

@RestController
@RequestMapping("api/v1/rides")
public class RideController {

    @Autowired
    RideService rideService;

    @GetMapping(value = "/{id}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<RideTrackingDTO> getRideLocation(@PathVariable Long id) {
        RideTrackingDTO response = rideService.getRideTrackingInfo(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/inconsistency-report", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<String> reportInconsistency(
            @PathVariable Long id, 
            @RequestBody InconsistencyReportRequestDTO request) {
                boolean isReported = rideService.reportInconsistency(id, request);
                
                if (isReported) {
                    return new ResponseEntity<>("Consistency report sent successfully.", HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>("Failed to send consistency report.", HttpStatus.INTERNAL_SERVER_ERROR);
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
    @PreAuthorize("hasRole('DRIVER', 'PASSENGER')")
    @PutMapping("/cancel-ride")
    public ResponseEntity<String> cancelRide(@RequestBody CancelRideDTO request){
        rideService.cancelRide(request);
        return ResponseEntity.ok("Ride cancelled!");
    }
    @PreAuthorize("hasRole('DRIVER', 'PASSENGER')")
    @PutMapping("/panic")
    public ResponseEntity<String> panic(@RequestBody PanicNotificationDTO request){
        rideService.handlePanicNotification(request);
        return ResponseEntity.ok("PANIC button activated!");
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/stop-ride")
    public ResponseEntity<String> panic(@RequestBody StopRideDTO request){
        rideService.stopRide(request);
        return ResponseEntity.ok("Ride stopped!");
    }


    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateRideResponseDTO> createRide(@RequestBody CreateRideRequestDTO requestDTO, Authentication auth){
        String email = auth.getName(); //email passengera, odnoson usera, koji je porucio voznju
        CreateRideResponseDTO responseDTO = rideService.createRide(requestDTO, email);
        return ResponseEntity.ok(responseDTO);
    }
}
