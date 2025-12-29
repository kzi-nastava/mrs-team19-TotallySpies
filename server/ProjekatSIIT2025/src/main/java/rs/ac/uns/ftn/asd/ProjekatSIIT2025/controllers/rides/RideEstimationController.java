package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideEstimationRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideEstimationResponseDTO;

@RestController
@RequestMapping("api/v1/ride-estimation")
public class RideEstimationController {
    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideEstimationResponseDTO> estimateRide(@RequestBody RideEstimationRequestDTO request){
        RideEstimationResponseDTO response = new RideEstimationResponseDTO();
        response.setEstimatedMoney(1500);
        response.setEstimatedTime(30);
        return new ResponseEntity<RideEstimationResponseDTO>(response, HttpStatus.OK);
    }
}
