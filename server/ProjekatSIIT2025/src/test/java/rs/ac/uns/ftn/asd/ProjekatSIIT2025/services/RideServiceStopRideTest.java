package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.StopRideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceStopRideTest {
    @Mock
    RideRepository rideRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    RideService rideService;

    private void mockLoggedInEmail(String email) {
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void stopRide_throws404_whenRideNotFound() {
        StopRideDTO dto = mock(StopRideDTO.class);
        when(dto.getRideId()).thenReturn(1L);
        when(rideRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.stopRide(dto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Ride not found!", ex.getReason());
        verifyNoInteractions(userRepository);
    }

    @Test
    void stopRide_throws404_whenUserNotFound() {
        mockLoggedInEmail("driver@mail.com");
        StopRideDTO dto = mock(StopRideDTO.class);
        when(dto.getRideId()).thenReturn(1L);
        Ride ride = new Ride();
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(userRepository.findByEmail("driver@mail.com")).thenReturn(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.stopRide(dto));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("User not found!", ex.getReason());
    }

    @Test
    void stopRide_throws400_whenEndTimeNull() {
        mockLoggedInEmail("driver@mail.com");
        StopRideDTO dto = mock(StopRideDTO.class);
        when(dto.getRideId()).thenReturn(1L);
        when(dto.getNewEndTime()).thenReturn(null);
        Ride ride = new Ride();
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(userRepository.findByEmail("driver@mail.com")).thenReturn(validUser("driver@mail.com"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.stopRide(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("End time can not be null!", ex.getReason());
    }
    @Test
    void stopRide_throws400_whenRideHasNoStops_null() {
        mockLoggedInEmail("driver@mail.com");
        StopRideDTO dto = mock(StopRideDTO.class);
        when(dto.getRideId()).thenReturn(1L);
        when(dto.getNewEndTime()).thenReturn(LocalDateTime.now());
        when(dto.getNewTotalPrice()).thenReturn(999.0);
        Ride ride = mock(Ride.class);
        when(ride.getStops()).thenReturn(null);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(userRepository.findByEmail("driver@mail.com")).thenReturn(validUser("driver@mail.com"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.stopRide(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Ride has no stops", ex.getReason());
    }

    @Test
    void stopRide_throws400_whenRideHasNoStops_empty() {
        mockLoggedInEmail("driver@mail.com");
        StopRideDTO dto = mock(StopRideDTO.class);
        when(dto.getRideId()).thenReturn(1L);
        when(dto.getNewEndTime()).thenReturn(LocalDateTime.now());
        when(dto.getNewTotalPrice()).thenReturn(999.0);
        Ride ride = new Ride();
        ride.setStops(new ArrayList<>());
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(userRepository.findByEmail("driver@mail.com")).thenReturn(validUser("driver@mail.com"));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> rideService.stopRide(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Ride has no stops", ex.getReason());
    }

    @Test
    void stopRide_success_updatesRideAndReplacesLastStop() {
        mockLoggedInEmail("driver@mail.com");
        StopRideDTO dto = mock(StopRideDTO.class);
        when(dto.getRideId()).thenReturn(1L);
        LocalDateTime newEnd = LocalDateTime.of(2026, 2, 18, 12, 0);
        when(dto.getNewEndTime()).thenReturn(newEnd);
        when(dto.getNewTotalPrice()).thenReturn(123.45);
        Ride ride = new Ride();
        RideStop s0 = new RideStop();
        s0.setOrderIndex(0);
        s0.setRide(ride);
        RideStop oldLast = new RideStop();
        oldLast.setOrderIndex(1);
        oldLast.setRide(ride);
        ride.setStops(new ArrayList<>(List.of(s0, oldLast)));
        RideStop newDest = new RideStop();
        newDest.setAddress("New destination");
        when(dto.getNewDestination()).thenReturn(newDest);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(userRepository.findByEmail("driver@mail.com")).thenReturn(validUser("driver@mail.com"));
        rideService.stopRide(dto);
        assertEquals(newEnd, ride.getFinishedAt());
        assertEquals(123.45, ride.getTotalPrice());
        assertEquals(RideStatus.STOPPED, ride.getStatus());
        assertEquals(2, ride.getStops().size());
        assertNull(oldLast.getRide(), "Removed stop should be detached (ride=null)");
        RideStop last = ride.getStops().get(1);
        assertSame(newDest, last);
        assertSame(ride, newDest.getRide());
        assertEquals(1, newDest.getOrderIndex());
    }

    private User validUser(String email) {
        User u = new User();
        u.setName("Test");
        u.setLastName("Driver");
        u.setEmail(email);
        u.setPassword("x");
        u.setRole(UserRole.DRIVER);
        return u;
    }

}
