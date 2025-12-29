package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.InconsistencyReportRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideFinishResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideStartResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.VehicleDisplayResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

@RestController
@RequestMapping("api/v1/rides")
public class RideController {
    
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
        RideFinishResponseDTO response = new RideFinishResponseDTO();
        response.setRideId(id);
        response.setFinishTime(LocalDateTime.now());
        response.setStatus(RideStatus.COMPLETED);
        response.setTotalPrice(350.00);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/scheduled", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RideFinishResponseDTO>> getFutureRides() {
        List<RideFinishResponseDTO> futureRides = new ArrayList<>();
        
        RideFinishResponseDTO ride = new RideFinishResponseDTO();
        ride.setFinishTime(LocalDateTime.now());
        ride.setRideId(55L);
        ride.setStatus(RideStatus.SCHEDULED);
        ride.setTotalPrice(450.00);
        futureRides.add(ride);
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
}
