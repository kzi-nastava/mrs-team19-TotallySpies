package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.drivers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.DriverRideHistoryResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverActivityResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.VehicleInfoResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.DriverActivityService;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.DriverService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/drivers")
public class DriverController {
    @Autowired
    DriverActivityService driverActivityService;

    @Autowired
    DriverService driverService;

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

    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping(value = "/activity")
    public ResponseEntity<DriverActivityResponseDTO> getActivity(Authentication auth){
        String email = auth.getName();
        DriverActivityResponseDTO dto = driverActivityService.getActivityMinutesLast24h(email);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping("/vehicle-info")
    public ResponseEntity<VehicleInfoResponseDTO> getVehicleInfo(Authentication auth) {
        String email = auth.getName();
        VehicleInfoResponseDTO dto = driverService.getVehicleInfo(email);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DriverProfileResponseDTO> getDriverProfile(@PathVariable Long id) {
        DriverProfileResponseDTO dto = new DriverProfileResponseDTO();
        dto.setId(id);
        dto.setName("DriverName");
        dto.setLastName("DriverLast");
        dto.setEmail("driver@gmail.com");
        dto.setProfilePicture("driver.png");
        dto.setVehicleModel("Skoda Octavia");
        dto.setLicensePlate("NS-123-YY");
        return ResponseEntity.ok(dto);
    }
}