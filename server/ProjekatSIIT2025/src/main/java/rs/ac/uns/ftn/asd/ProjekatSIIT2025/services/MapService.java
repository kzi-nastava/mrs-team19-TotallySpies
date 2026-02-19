package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.VehicleDisplayResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;

@Service
public class MapService {
    @Autowired
    private DriverRepository driverRepository;

    @Transactional
    public List<VehicleDisplayResponseDTO> getActiveVehicles() {
        List<Driver> activeDrivers = driverRepository.findByIsActiveTrue();
        List<VehicleDisplayResponseDTO> markers = new ArrayList<>();

        for (Driver driver : activeDrivers) {
            if (driver.getVehicle() == null) {
                continue;
            }
            boolean isBusy = false;
            for (Ride ride : driver.getRides()) {
                if (ride.getStatus() == RideStatus.ACTIVE) {
                    isBusy = true;
                    break;
                }
            }
            markers.add(new VehicleDisplayResponseDTO(
                driver.getVehicle().getId(),
                driver.getId(),
                driver.getVehicle().getCurrentLat(),
                driver.getVehicle().getCurrentLng(),
                isBusy,
                driver.getName()
            ));
        }
        return markers;
    }

}
