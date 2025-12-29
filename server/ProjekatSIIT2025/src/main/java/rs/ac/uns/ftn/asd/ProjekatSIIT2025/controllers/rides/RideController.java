package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.InconsistencyReportRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.VehicleDisplayResponseDTO;

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
}
