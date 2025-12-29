package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.LocationDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RidePreviewResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideRejectionDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/v1/ride-rejection")
public class RideRejectionController {
    @PostMapping(value = "/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideDTO> declineRide(@RequestBody RideRejectionDTO request){
        //check if it is 10 minutes before a ride and the user that is rejecting a ride is a passenger
        RideRejection rejection = new RideRejection(1L, request.getUserId(), request.getRideId(),
                request.getRejectionReason(), request.getTime());
        RideDTO response = new RideDTO();
        response.setId(2L);
        response.setStartTime(LocalDateTime.of(2025, 1, 10, 14, 30));
        response.setEndTime(LocalDateTime.of(2025, 1, 10, 14, 55));
        response.setTotalPrice(2000);
        response.setRiderId(5L);
        response.setPassengers(new ArrayList<>(List.of(
                7L,
                8L
        )));

        response.setPaths(new ArrayList<>(List.of(
                101L,
                102L
        )));

        response.setReviews(new ArrayList<>(List.of(
                201L
        )));

        response.setStatus(RideStatus.CANCELLED);
        response.setPanic(false);
        response.setBabiesTransport(true);
        response.setPetsTransport(false);
        response.setVehicleType(VehicleType.VAN);
        response.setReports(new ArrayList<>(List.of(
                301L
        )));
        response.setRideRejectionId(rejection.getId());
        return new ResponseEntity<RideDTO>(response, HttpStatus.OK);

    }
    @PostMapping(value = "/stop/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideResponseDTO> stopRide(@PathVariable Long id, @RequestBody LocationDTO request){
        RideResponseDTO response = new RideResponseDTO();
        Location location = new Location(request.getId(), request.getLongitude(), request.getLatitude());
        response.setId(id);
        response.setEndTime(LocalDateTime.of(2025, 1, 10, 14, 30));
        response.setTotalPrice(1500);
        response.getPaths().remove(response.getPaths().size()-1);
        response.getPaths().add(location.getId());
        response.setStatus(RideStatus.STOPPED);
        return new ResponseEntity<RideResponseDTO>(response, HttpStatus.OK);
    }

}
