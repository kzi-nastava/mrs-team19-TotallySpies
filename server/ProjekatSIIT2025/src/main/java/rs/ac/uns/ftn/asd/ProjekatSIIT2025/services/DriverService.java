package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.VehicleInfoResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Vehicle;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;

@Service
public class DriverService {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    DriverActivityService driverActivityService;

    public void setActiveDriver(String email){
        Driver driver = driverRepository.findByEmail(email);
        if (driver == null) {
            throw new RuntimeException("Driver not found");
        }
        if (!driver.isActive()) return;

        driver.setActive(true);
        driverRepository.save(driver);

        // start nove aktivnosti
        driverActivityService.startActivity(driver);
    }

    public void setInactiveDriver(String email){
        Driver driver = driverRepository.findByEmail(email);

        if (driver == null) {
            throw new RuntimeException("Driver not found");
        }

        if (!driver.isActive()) return;

        driver.setActive(false);
        driverRepository.save(driver);

        // end aktivnosti koja ima null za endTime
        driverActivityService.endActivity(driver);
    }

    public VehicleInfoResponseDTO getVehicleInfo(String driverEmail){
        Driver driver = driverRepository.findByEmail(driverEmail);

        if (driver == null) {
            throw new RuntimeException("Driver not found");
        }

        Vehicle vehicle = driver.getVehicle();

        VehicleInfoResponseDTO dto = new VehicleInfoResponseDTO();
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setModel(vehicle.getModel());
        dto.setBabyTransport(vehicle.isBabyTransport());
        dto.setPetTransport(vehicle.isPetTransport());
        dto.setPassengerCapacity(vehicle.getPassengerCapacity());
        dto.setVehicleType(vehicle.getVehicleType());

        return dto;
    }
}
