package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.VehicleRepository;

@Service
public class VehicleSimulationService {
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Scheduled(fixedRate = 7000)
    @Transactional
    public void simulateMovement() {
        List<Driver> drivers = driverRepository.findByIsActiveTrue();
        for (Driver driver : drivers) {
            if (driver.getVehicle() == null) continue;

            boolean isBusy = false;
            for (Ride ride : driver.getRides()) {
                if (ride.getStatus() == RideStatus.ACTIVE) {
                    isBusy = true;
                    break;
                }
            }
            double latChange;
            double lngChange;

            if (isBusy) {
                latChange = (Math.random() - 0.5) * 0.005; 
                lngChange = (Math.random() - 0.5) * 0.005;
                // implement linear interpolation
            }
            else {
                latChange = (Math.random() - 0.5) * 0.0005;
                lngChange = (Math.random() - 0.5) * 0.0005;
            }
            driver.getVehicle().setCurrentLat(driver.getVehicle().getCurrentLat() + latChange);
            driver.getVehicle().setCurrentLng(driver.getVehicle().getCurrentLng() + lngChange);
            vehicleRepository.save(driver.getVehicle());
        }
    }
    
}
