package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.VehicleDisplayResponseDTO;

@RestController
@RequestMapping("api/v1/vehicles")
public class VehicleDisplayController {
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VehicleDisplayResponseDTO>> getAllVehicles(){

        List<VehicleDisplayResponseDTO> vehicles = new ArrayList<>();
        vehicles.add(new VehicleDisplayResponseDTO(1L, 45.2671, 19.8335, false));
        vehicles.add(new VehicleDisplayResponseDTO(2L, 44.7866, 20.4489, true));
        vehicles.add(new VehicleDisplayResponseDTO(3L, 43.8563, 18.4131, false));

        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }
}
