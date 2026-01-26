package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.RideService;

@RestController
@RequestMapping("api/v1/rides")
public class RideController {

    @Autowired
    RideService rideService;

    @GetMapping(value = "/{id}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VehicleDisplayResponseDTO> getRideLocation(@PathVariable Long id) {
        VehicleDisplayResponseDTO response = new VehicleDisplayResponseDTO();
        response.setId(id); 
        response.setCurrentLat(45.2396);
        response.setCurrentLng(19.8227);
        response.setBusy(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/consistency-report", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> reportInconsistency(
            @PathVariable Long id, 
            @RequestBody InconsistencyReportRequestDTO request) {
                
        return new ResponseEntity<>("Consistency report sent successfully.", HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}/end", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideFinishResponseDTO> finishRide(@PathVariable Long id) {
        RideFinishResponseDTO response = rideService.finishRide(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/scheduled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RideFinishResponseDTO>> getFutureRides() {
        List<RideFinishResponseDTO> futureRides = rideService.findScheduledRides();
        return new ResponseEntity<>(futureRides, HttpStatus.OK);
    }

    @PutMapping("/{rideId}/start")
    public ResponseEntity<RideStartResponseDTO> startRide(@PathVariable Long rideId) {
        RideStartResponseDTO response = new RideStartResponseDTO();
        response.setRideId(rideId);
        response.setStatus("STARTED");
        response.setMessage("Ride has successfully started.");
        return ResponseEntity.ok(response);
    }
    @PutMapping("/cancel-ride")
    public ResponseEntity<String> cancelRide(@RequestBody CancelRideDTO request){
        rideService.cancelRide(request);
        return ResponseEntity.ok("Ride cancelled!");
    }
    @PutMapping("/panic")
    public ResponseEntity<String> panic(@RequestBody PanicNotificationDTO request){
        rideService.handlePanicNotification(request);
        return ResponseEntity.ok("PANIC button activated!");
    }

    @PostMapping(value = "/create")
    public ResponseEntity<CreateRideResponseDTO> createRide(@RequestBody CreateRideRequestDTO requestDTO, Authentication auth){
        String email = auth.getName(); //email passengera, odnoson usera, koji je porucio voznju
        CreateRideResponseDTO responseDTO = rideService.createRide(requestDTO, email);
        return ResponseEntity.ok(responseDTO);
    }
}
