package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.VehicleDisplayResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.MapService;

@RestController
@RequestMapping("api/v1/vehicles")
public class VehicleDisplayController {
    @Autowired
    private MapService mapService;
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<VehicleDisplayResponseDTO>> getAllVehicles(){
        return new ResponseEntity<>(mapService.getActiveVehicles(), HttpStatus.OK);
    }
}
