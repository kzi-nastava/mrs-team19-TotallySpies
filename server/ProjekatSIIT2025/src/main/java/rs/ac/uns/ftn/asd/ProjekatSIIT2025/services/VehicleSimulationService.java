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
    private static final double SPEED = 0.002;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void simulateMovement() {
        List<Driver> drivers = driverRepository.findByIsActiveTrue();
        for (Driver driver : drivers) {
            if (driver.getVehicle() == null) continue;

            Ride activeRide = driver.getRides().stream()
                .filter(r -> r.getStatus() == RideStatus.ACTIVE)
                .findFirst()
                .orElse(null);

            if (activeRide != null) {
                var stops = activeRide.getStops(); 
                if (stops.isEmpty()) continue;
                
                var destination = stops.get(stops.size() - 1); 
                
                double currentLat = driver.getVehicle().getCurrentLat();
                double currentLng = driver.getVehicle().getCurrentLng();
                double destLat = destination.getLatitude();
                double destLng = destination.getLongitude();

                double distance = Math.sqrt(Math.pow(destLat - currentLat, 2) + Math.pow(destLng - currentLng, 2));

                if (distance < SPEED) {
                    // if the destination is there
                    driver.getVehicle().setCurrentLat(destLat);
                    driver.getVehicle().setCurrentLng(destLng);
                    // change status?
                } else {
                    double ratio = SPEED / distance;
                    double newLat = currentLat + (destLat - currentLat) * ratio;
                    double newLng = currentLng + (destLng - currentLng) * ratio;
                    
                    driver.getVehicle().setCurrentLat(newLat);
                    driver.getVehicle().setCurrentLng(newLng);
                }

            } else {
                double latChange = (Math.random() - 0.5) * 0.0005;
                double lngChange = (Math.random() - 0.5) * 0.0005;
                driver.getVehicle().setCurrentLat(driver.getVehicle().getCurrentLat() + latChange);
                driver.getVehicle().setCurrentLng(driver.getVehicle().getCurrentLng() + lngChange);
            }
            vehicleRepository.save(driver.getVehicle());
        }
    }
    
}
