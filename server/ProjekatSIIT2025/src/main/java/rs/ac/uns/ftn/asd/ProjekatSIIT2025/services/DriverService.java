package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.MailBody;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.CreateDriverRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverBlockedStatusDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.VehicleInfoResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ActivationToken;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.UserRole;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Vehicle;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ActivationTokenRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class DriverService {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    DriverActivityService driverActivityService;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    ActivationTokenRepository activationTokenRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public void setActiveDriver(String email){
        Driver driver = driverRepository.findByEmail(email);
        if (driver == null) {
            throw new RuntimeException("Driver not found");
        }
        if (driver.isActive()) return;
        driver.setActive(true);
        driverRepository.save(driver);

        // start nove aktivnosti
        driverActivityService.startActivity(driver);
    }

    @Transactional
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

    @Transactional
    public void createDriver(CreateDriverRequestDTO dto) {

        if (driverRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        // 1. kreiraj vozilo
        Vehicle vehicle = new Vehicle();
        vehicle.setBabyTransport(dto.isBabyFriendly());
        vehicle.setVehicleType(dto.getType());
        vehicle.setModel(dto.getModel());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setPassengerCapacity(dto.getSeats());
        vehicle.setPetTransport(dto.isPetFriendly());
        vehicleRepository.save(vehicle);

        // 2. kreiraj user/driver
        Driver driver = new Driver();
        driver.setVehicle(vehicle);
        driver.setRole(UserRole.DRIVER);
        driver.setEnabled(false);
        driver.setBlocked(false);
        driver.setAddress(dto.getAddress());
        driver.setEmail(dto.getEmail());
        driver.setLastName(dto.getLastName());
        driver.setName(dto.getFirstName());
        driver.setPhoneNumber(dto.getPhone());
        driver.setActive(false);
        driver.setPassword(encoder.encode("TEMP"));
        driverRepository.save(driver);

        // 3. activation token
        String token = UUID.randomUUID().toString().replace("-", "");

        ActivationToken activationToken = new ActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(driver);
        activationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        activationTokenRepository.save(activationToken);

        // 4. mail
        String activationLink = "http://localhost:4200/activate-driver-account?token=" + token;

        MailBody mailBody = MailBody.builder()
                .to(driver.getEmail())
                .subject("Activate your driver account")
                .text("Set your password here (valid 24h): " + activationLink)
                .build();

        emailService.sendSimpleMessage(mailBody);
    }

    public DriverBlockedStatusDTO getBlockStatus(String email){
        Driver driver = driverRepository.findByEmail(email);

        if (driver == null) {
            throw new RuntimeException("Driver not found");
        }

        DriverBlockedStatusDTO dto = new DriverBlockedStatusDTO();
        dto.setBlockReason(driver.getBlockReason());
        dto.setBlocked(driver.isBlocked());
        return dto;
    }
}
