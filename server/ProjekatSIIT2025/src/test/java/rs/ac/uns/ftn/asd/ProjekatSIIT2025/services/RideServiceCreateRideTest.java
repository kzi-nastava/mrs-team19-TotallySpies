package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.CreateRideRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.CreateRideResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideStopDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverActivityResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceCreateRideTest {
    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private PricingService pricingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private DriverActivityService driverActivityService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private RideService rideService;

    private Passenger creator;
    private Driver driver;
    private Vehicle vehicle;
    private CreateRideRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        // kreator voznje
        creator = new Passenger();
        creator.setId(1L);
        creator.setEmail("passenger@test.com");
        creator.setName("Ana");
        creator.setLastName("Anić");
        creator.setBlocked(false);

        // kreiranje vozila za vozace
        vehicle = new Vehicle();
        vehicle.setCurrentLat(45.25);
        vehicle.setCurrentLng(19.83);
        vehicle.setVehicleType(VehicleType.STANDARD);
        vehicle.setPassengerCapacity(4);
        vehicle.setPetTransport(false);
        vehicle.setBabyTransport(false);

        // kreiranje vozaca
        driver = new Driver();
        driver.setId(10L);
        driver.setEmail("driver@test.com");
        driver.setName("Marko");
        driver.setLastName("Marković");
        driver.setActive(true);
        driver.setVehicle(vehicle);

        // validan zahtjev za kreiranje voznje
        validRequest = new CreateRideRequestDTO();
        validRequest.setLocations(List.of(
                new RideStopDTO(45.25, 19.83, "Polaziste"),
                new RideStopDTO(45.26, 19.85, "Odrediste")
        ));
        validRequest.setVehicleType(VehicleType.STANDARD);
        validRequest.setDistanceKm(5.0);
        validRequest.setEstimatedTime(10.0);
        validRequest.setBabyTransport(false);
        validRequest.setPetTransport(false);
        validRequest.setPassengerEmails(null);
    }

    @Test
    void createRide_validRequestWithAvailableDriver_returnsScheduledRide() {
        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(false);
        when(driverRepository.findPotentialDrivers(any(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(driver));
        DriverActivityResponseDTO activityResponse = new DriverActivityResponseDTO();
        activityResponse.setMinutesLast24h(0L);
        when(driverActivityService.getActivityMinutesLast24h(anyString())).thenReturn(activityResponse);
        when(rideRepository.findByDriverAndStatusIn(eq(driver), any())).thenReturn(List.of());
        when(pricingService.calculatePrice(any(), anyDouble())).thenReturn(720.0);
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> {
            Ride r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });
        when(userRepository.findByEmail("driver@test.com")).thenReturn(driver);

        CreateRideResponseDTO result = rideService.createRide(validRequest, "passenger@test.com");

        assertNotNull(result);
        assertEquals(RideStatus.SCHEDULED, result.getStatus());
        assertEquals("driver@test.com", result.getDriverEmail());
        assertEquals("Marko Marković", result.getDriverName());
        assertTrue(result.getMessage().contains("driver assigned"));

        verify(rideRepository, times(1)).save(any(Ride.class));
        verify(notificationService, atLeastOnce()).notifyUser(any(), any(), anyString(), any());
    }

    @Test
    void createRide_withAdditionalPassengers_allValid_returnsScheduledRide() {
        Passenger secondPassenger = new Passenger();
        secondPassenger.setId(2L);
        secondPassenger.setEmail("other@test.com");
        secondPassenger.setName("Petra");
        secondPassenger.setBlocked(false);

        validRequest.setPassengerEmails(List.of("other@test.com"));

        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(passengerRepository.findByEmail("other@test.com")).thenReturn(Optional.of(secondPassenger));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(false);
        when(rideRepository.existsByPassengersContainingAndStatus(secondPassenger, RideStatus.ACTIVE)).thenReturn(false);
        when(driverRepository.findPotentialDrivers(any(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(driver));
        DriverActivityResponseDTO activityResponse = new DriverActivityResponseDTO();
        activityResponse.setMinutesLast24h(0L);
        when(driverActivityService.getActivityMinutesLast24h(anyString())).thenReturn(activityResponse);
        when(rideRepository.findByDriverAndStatusIn(eq(driver), any())).thenReturn(List.of());
        when(pricingService.calculatePrice(any(), anyDouble())).thenReturn(720.0);
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> {
            Ride r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });
        when(userRepository.findByEmail("driver@test.com")).thenReturn(driver);

        CreateRideResponseDTO result = rideService.createRide(validRequest, "passenger@test.com");

        assertNotNull(result);
        assertEquals(RideStatus.SCHEDULED, result.getStatus());
    }

    @Test
    void createRide_passengerNotFound_throws404() {
        when(passengerRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.createRide(validRequest, "unknown@test.com"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Passenger not found"));
    }

    @Test
    void createRide_passengerBlocked_throws400() {
        creator.setBlocked(true);
        creator.setBlockReason("Kršenje pravila");

        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.createRide(validRequest, "passenger@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("blocked"));
    }

    @Test
    void createRide_creatorHasActiveRide_throws400() {
        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.createRide(validRequest, "passenger@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("active ride"));
    }

    @Test
    void createRide_additionalPassengerNotFound_throws404() {
        validRequest.setPassengerEmails(List.of("ghost@test.com"));

        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(false);
        when(passengerRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.createRide(validRequest, "passenger@test.com"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("ghost@test.com"));
    }

    @Test
    void createRide_scheduledMoreThan5HoursAhead_throws400() {
        validRequest.setScheduledFor(LocalDateTime.now().plusHours(6));

        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.createRide(validRequest, "passenger@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("5 hours"));
    }

    @Test
    void createRide_scheduledInPast_throws400() {
        validRequest.setScheduledFor(LocalDateTime.now().minusMinutes(1));

        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.createRide(validRequest, "passenger@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("future"));
    }


    @Test
    void createRide_scheduledWithin30Minutes_remainsPending() {
        validRequest.setScheduledFor(LocalDateTime.now().plusMinutes(20));

        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(false);
        when(pricingService.calculatePrice(any(), anyDouble())).thenReturn(720.0);
        when(rideRepository.save(any(Ride.class))).thenAnswer(inv -> {
            Ride r = inv.getArgument(0);
            r.setId(4L);
            return r;
        });

        CreateRideResponseDTO result = rideService.createRide(validRequest, "passenger@test.com");

        assertNotNull(result);
        assertEquals(RideStatus.PENDING, result.getStatus());
        assertNull(result.getDriverEmail());
    }

    @Test
    void createRide_noAvailableDrivers_throws400() {
        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(false);
        when(driverRepository.findPotentialDrivers(any(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.createRide(validRequest, "passenger@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("No available drivers"));
    }

    @Test
    void createRide_driverHasScheduledRide_notAssigned_throws400() {
        Ride scheduledRide = new Ride();
        scheduledRide.setStatus(RideStatus.SCHEDULED);

        when(passengerRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(creator));
        when(rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE)).thenReturn(false);
        when(driverRepository.findPotentialDrivers(any(), anyInt(), anyBoolean(), anyBoolean()))
                .thenReturn(List.of(driver));
        when(rideRepository.findByDriverAndStatusIn(eq(driver), any())).thenReturn(List.of(scheduledRide));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.createRide(validRequest, "passenger@test.com"));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}