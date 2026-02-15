package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideFinishResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock private RideRepository rideRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks
    private RideService rideService;

    @Captor
    private ArgumentCaptor<Ride> rideArgumentCaptor;


    @Test
    @DisplayName("2.7.1 - succsesfully finish ride and find next one")
    void finishRide_SuccessWithNextRide() {
        Long rideId = 1L;
        Driver driver = new Driver(); 
        driver.setId(100L);
        driver.setEmail("driver@gmail.com");
        Ride ride = createRide(rideId, RideStatus.ACTIVE, driver);
        Ride nextRide = createRide(2L, RideStatus.SCHEDULED, driver);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepository.findByEmail("driver@gmail.com")).thenReturn(driver);
        when(rideRepository.findFirstByDriverIdAndStatusOrderByStartedAtAsc(100L, RideStatus.SCHEDULED))
                .thenReturn(Optional.of(nextRide));

        mockSecurityContext("driver@gmail.com");
        RideFinishResponseDTO response = rideService.finishRide(rideId);

        assertEquals(RideStatus.COMPLETED, response.getStatus());
        assertEquals(2L, response.getNextRideId());
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    @DisplayName("2.7.2 - successfully finish ride and no next ride")
    void finishRide_NoNextRide_NextIdIsNull() {
                Long rideId = 1L;
        Driver driver = new Driver(); 
        driver.setId(100L);
        driver.setEmail("driver@gmail.com");
        Ride ride = createRide(rideId, RideStatus.ACTIVE, driver);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepository.findByEmail("driver@gmail.com")).thenReturn(driver);
        when(rideRepository.findFirstByDriverIdAndStatusOrderByStartedAtAsc(100L, RideStatus.SCHEDULED))
                .thenReturn(Optional.empty());

        mockSecurityContext("driver@gmail.com");
        RideFinishResponseDTO response = rideService.finishRide(rideId);

        assertNull(response.getNextRideId());
    }

    @Test
    @DisplayName("2.7.3 - nonexistent ride ID (404)")
    void finishRide_InvalidId_ThrowsNotFound() {
        when(rideRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> rideService.finishRide(999L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("2.7.4 - no passengers")
    void finishRide_NoPassengers_NoNotifications() {
                Long rideId = 1L;
        Driver driver = new Driver();
        driver.setId(100L);
        driver.setEmail("driver@gmail.com");
        Ride ride = createRide(rideId, RideStatus.ACTIVE, driver);
        ride.setPassengers(new ArrayList<>()); 

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepository.findByEmail("driver@gmail.com")).thenReturn(driver);

        mockSecurityContext("driver@gmail.com");
        rideService.finishRide(rideId);

        verify(notificationService, never()).notifyUser(any(), any(), any(), any());
        verify(emailService, never()).sendSimpleMessage(any());
    }

    @Test
    @DisplayName("2.7.5 - error while sending mail but ride still completes")
    void finishRide_EmailFails_StillCompletes() {
                Long rideId = 1L;
        Driver driver = new Driver();
        driver.setId(100L);
        driver.setEmail("driver@gmail.com");
        Ride ride = createRide(rideId, RideStatus.ACTIVE, driver);
        
        List<Passenger> passengers = new ArrayList<>();
        Passenger passenger = new Passenger();
        passenger.setEmail("passenger@example.com");
        passenger.setName("Test Passenger");
        passengers.add(passenger);
        ride.setPassengers(passengers);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepository.findByEmail("driver@gmail.com")).thenReturn(driver);
        doThrow(new RuntimeException("mail error")).when(emailService).sendSimpleMessage(any());

        mockSecurityContext("driver@gmail.com");
        RideFinishResponseDTO response = rideService.finishRide(rideId);

        assertEquals(RideStatus.COMPLETED, response.getStatus());
        verify(rideRepository).save(any());
    }


    @Test
    @DisplayName("2.7.6 - driver without rides")
    void findScheduledRides_NoRides_EmptyList() {
        mockSecurityContext("driver@gmail.com");
        Driver d = new Driver(); d.setId(100L);
        when(driverRepository.findByEmail("driver@gmail.com")).thenReturn(d);
        when(rideRepository.findByDriverIdAndStatusIn(eq(100L), anyList())).thenReturn(new ArrayList<>());

        List<RideFinishResponseDTO> result = rideService.findScheduledRides();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("2.7.7 - start and end stop same")
    void findScheduledRides_OneStop_SameLocations() {
        mockSecurityContext("driver@gmail.com");
        Ride ride = createRide(1L, RideStatus.ACTIVE, new Driver());
        RideStop stop = new RideStop(); stop.setAddress("Micurinova 30, Novi Sad");
        ride.setStops(List.of(stop));

        when(driverRepository.findByEmail(anyString())).thenReturn(new Driver());
        when(rideRepository.findByDriverIdAndStatusIn(any(), any())).thenReturn(List.of(ride));

        List<RideFinishResponseDTO> result = rideService.findScheduledRides();

        assertEquals("Micurinova 30, Novi Sad", result.get(0).getStartLocation());
        assertEquals("Micurinova 30, Novi Sad", result.get(0).getEndLocation());
    }

    @Test
    @DisplayName("2.7.8 - display active and scheduled times")
    void findScheduledRides_DisplaysCorrectTime() {
        mockSecurityContext("driver@gmail.com");
        LocalDateTime now = LocalDateTime.now();
        
        Ride active = createRide(1L, RideStatus.ACTIVE, new Driver());
        active.setStartedAt(now);
        
        Ride scheduled = createRide(2L, RideStatus.SCHEDULED, new Driver());
        scheduled.setScheduledFor(now.plusHours(1));

        when(driverRepository.findByEmail(anyString())).thenReturn(new Driver());
        when(rideRepository.findByDriverIdAndStatusIn(any(), any())).thenReturn(List.of(active, scheduled));

        List<RideFinishResponseDTO> result = rideService.findScheduledRides();

        assertEquals(now, result.get(0).getDisplayTime()); // StartedAt
        assertEquals(now.plusHours(1), result.get(1).getDisplayTime()); // ScheduledFor
    }

    @Test
    @DisplayName("2.7.9 - finish ride that is SCHEDULED (not ACTIVE) - still completes")
    void finishRide_RideNotActive_StillCompletes() {
        Long rideId = 1L;
        Driver driver = new Driver();
        driver.setId(100L);
        driver.setEmail("driver@gmail.com");
        Ride ride = createRide(rideId, RideStatus.SCHEDULED, driver); // nije ACTIVE

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepository.findByEmail("driver@gmail.com")).thenReturn(driver);

        RideFinishResponseDTO response = rideService.finishRide(rideId);

        assertEquals(RideStatus.COMPLETED, response.getStatus());
        verify(rideRepository).save(rideArgumentCaptor.capture());
        assertEquals(RideStatus.COMPLETED, rideArgumentCaptor.getValue().getStatus());
    }

    @Test
    @DisplayName("2.7.10 - different driver tries to finish ride -> 403 FORBIDDEN")
    void finishRide_DifferentDriver_ThrowsForbidden() {
        Long rideId = 1L;
        Driver rideDriver = new Driver();
        rideDriver.setId(100L);
        rideDriver.setEmail("driver.owner@gmail.com");
        
        Driver currentDriver = new Driver();
        currentDriver.setId(200L);
        currentDriver.setEmail("driver.other@gmail.com");

        Ride ride = createRide(rideId, RideStatus.ACTIVE, rideDriver);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(driverRepository.findByEmail("driver.other@gmail.com")).thenReturn(currentDriver);

        mockSecurityContext("driver.other@gmail.com");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.finishRide(rideId));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    @DisplayName("2.7.11 - findScheduledRides with null stops - no NPE")
    void findScheduledRides_NullStops_HandledGracefully() {
        mockSecurityContext("driver@gmail.com");
        Driver driver = new Driver();
        driver.setId(100L);
        
        Ride ride = createRide(1L, RideStatus.ACTIVE, driver);
        ride.setStops(null);

        when(driverRepository.findByEmail("driver@gmail.com")).thenReturn(driver);
        when(rideRepository.findByDriverIdAndStatusIn(eq(100L), anyList())).thenReturn(List.of(ride));

        List<RideFinishResponseDTO> result = rideService.findScheduledRides();

        assertEquals(1, result.size());
        assertNull(result.get(0).getStartLocation());
        assertNull(result.get(0).getEndLocation());
    }

    @Test
    @DisplayName("2.7.12 - findScheduledRides with empty stops - start/end location null")
    void findScheduledRides_EmptyStops_LocationNull() {
        mockSecurityContext("driver@gmail.com");
        Driver driver = new Driver();
        driver.setId(100L);
        
        Ride ride = createRide(1L, RideStatus.ACTIVE, driver);
        ride.setStops(new ArrayList<>());

        when(driverRepository.findByEmail("driver@gmail.com")).thenReturn(driver);
        when(rideRepository.findByDriverIdAndStatusIn(eq(100L), anyList())).thenReturn(List.of(ride));

        List<RideFinishResponseDTO> result = rideService.findScheduledRides();

        assertEquals(1, result.size());
        assertNull(result.get(0).getStartLocation());
        assertNull(result.get(0).getEndLocation());
    }

    // helper methods

    private void mockSecurityContext(String email) {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(auth.getName()).thenReturn(email);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    private Ride createRide(Long id, RideStatus status, Driver driver) {
        Ride r = new Ride();
        r.setId(id);
        r.setStatus(status);
        r.setDriver(driver);
        r.setPassengers(new ArrayList<>());
        r.setStops(new ArrayList<>());
        return r;
    }
}