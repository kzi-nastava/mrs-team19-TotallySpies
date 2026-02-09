package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.VehicleDisplayResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideTrackingDTO; // DODAJ OVO
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RoutePoint;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.VehicleRepository;

@Service
public class VehicleSimulationService {
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedRate = 2000)
    @Transactional
    public void simulateMovement() {
        List<Driver> drivers = driverRepository.findByIsActiveTrue();
        List<VehicleDisplayResponseDTO> allVehiclesUpdates = new ArrayList<>();

        for (Driver driver : drivers) {
            if (driver.getVehicle() == null) continue;

            Ride activeRide = driver.getRides().stream()
                    .filter(r -> r.getStatus() == RideStatus.ACTIVE)
                    .findFirst()
                    .orElse(null);

            if (activeRide != null && activeRide.getRoutePoints() != null && !activeRide.getRoutePoints().isEmpty()) {
                List<RoutePoint> route = activeRide.getRoutePoints();
                int currentIndex = driver.getVehicle().getCurrentRouteIndex();

                if (currentIndex < route.size()) {
                    RoutePoint targetPoint = route.get(currentIndex);
                    driver.getVehicle().setCurrentLat(targetPoint.getLatitude());
                    driver.getVehicle().setCurrentLng(targetPoint.getLongitude());
                    driver.getVehicle().setCurrentRouteIndex(currentIndex + 1);
                } else {
                    driver.getVehicle().setCurrentRouteIndex(0);
                }
            } else {
                double latChange = (Math.random() - 0.5) * 0.0005;
                double lngChange = (Math.random() - 0.5) * 0.0005;
                driver.getVehicle().setCurrentLat(driver.getVehicle().getCurrentLat() + latChange);
                driver.getVehicle().setCurrentLng(driver.getVehicle().getCurrentLng() + lngChange);
            }

            vehicleRepository.save(driver.getVehicle());

            if (activeRide != null) {
                try {
                    int totalPoints = activeRide.getRoutePoints().size();
                    int remainingPoints = totalPoints - driver.getVehicle().getCurrentRouteIndex();
                    int etaMinutes = Math.max(1, (remainingPoints * 2) / 60);

                    RideTrackingDTO trackingDTO = new RideTrackingDTO(
                        activeRide.getId(),
                        driver.getVehicle().getCurrentLat(),
                        driver.getVehicle().getCurrentLng(),
                        etaMinutes,
                        activeRide.getStatus().toString(),
                        driver.getName(),
                        driver.getVehicle().getModel(),
                        driver.getProfilePicture(),
                        activeRide.getStops().get(0).getAddress(),
                        activeRide.getStops().get(activeRide.getStops().size() - 1).getAddress()
                    );

                    redisTemplate.convertAndSend("ride-updates", objectMapper.writeValueAsString(trackingDTO));
                } catch (Exception e) {
                    System.err.println("error: " + e.getMessage());
                }
            }

            allVehiclesUpdates.add(new VehicleDisplayResponseDTO(
                    driver.getVehicle().getId(),
                    driver.getVehicle().getCurrentLat(),
                    driver.getVehicle().getCurrentLng(),
                    activeRide != null
            ));
        }

        try {
            String jsonUpdates = objectMapper.writeValueAsString(allVehiclesUpdates);
            redisTemplate.convertAndSend("vehicle-locations", jsonUpdates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}