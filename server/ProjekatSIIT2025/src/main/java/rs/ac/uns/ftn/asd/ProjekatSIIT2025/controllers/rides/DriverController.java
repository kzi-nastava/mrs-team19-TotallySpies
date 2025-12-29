package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.DriverRideHistoryResponseDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/drivers")
public class DriverController {

    @GetMapping(value = "/{id}/ride")
    public ResponseEntity<List<DriverRideHistoryResponseDTO>> getDriverRideHistory(
            @PathVariable Long id,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        
        System.out.println("Filters from: " + from + ", to: " + to);

        List<DriverRideHistoryResponseDTO> history = new ArrayList<>();
        
        DriverRideHistoryResponseDTO h1 = new DriverRideHistoryResponseDTO();
        h1.setId(101L);
        h1.setStartTime(LocalDateTime.now().minusDays(2));
        h1.setEndTime(LocalDateTime.now().minusDays(2).plusMinutes(30));
        h1.setPrice(850.0);
        h1.setPassengerEmail("passenger@gmail.com");
        
        history.add(h1);
        
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}