package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.CreateRideRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.CreateRideResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideStopDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.RideService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RideControllerCreateRideTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RideService rideService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    @DisplayName("2.4.1 autentifikovan putnik kreira voznju sa validnim podacima -> 200 OK i body sa rideId")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_validRequest_returns200() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();

        CreateRideResponseDTO response = new CreateRideResponseDTO();
        response.setRideId(1L);
        response.setStatus(RideStatus.SCHEDULED);
        response.setDriverName("Marko Marković");
        response.setDriverEmail("driver@test.com");
        response.setDistanceKm(5.0);
        response.setEstimatedTime(10.0);
        response.setMessage("Ride successfully created and driver assigned.");

        when(rideService.createRide(any(CreateRideRequestDTO.class), eq("passenger@test.com")))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rideId").value(1L))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.driverName").value("Marko Marković"))
                .andExpect(jsonPath("$.message").value("Ride successfully created and driver assigned."));
    }

    @Test
    @DisplayName("2.4.1 zahtijev bez lokacija (locations je null)-> 400")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_missingLocations_returns400() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();
        request.setLocations(null); // @NotNull treba da pukne

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.4.1 samo jedan lokacija umjesto minimum dvije -> 400")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_onlyOneLocation_returns400() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();
        request.setLocations(List.of(new RideStopDTO(19.0, 45.0, "Adresa 1")));

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.4.1 vehicleType je null -> 400")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_missingVehicleType_returns400() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();
        request.setVehicleType(null);

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.4.1 distanceKm je null -> 400")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_missingDistance_returns400() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();
        request.setDistanceKm(null);

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.4.1 estimatedTime je null -> 400")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_missingEstimatedTime_returns400() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();
        request.setEstimatedTime(null);

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.4.1 estimatedTime je nula — @Positive -> 400")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_zeroEstimatedTime_returns400() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();
        request.setEstimatedTime(0.0);

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.4.1 zakazana voznja u buducnosti -> vraća PENDING status i poruku o zakazivanju")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_scheduledRide_returnsPendingStatus() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();
        request.setScheduledFor(LocalDateTime.now().plusHours(2));

        CreateRideResponseDTO response = new CreateRideResponseDTO();
        response.setRideId(2L);
        response.setStatus(RideStatus.PENDING);
        response.setMessage("Ride successfully scheduled. You will receive notification 15 min before ride start.");

        when(rideService.createRide(any(), eq("passenger@test.com"))).thenReturn(response);

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Ride successfully scheduled. You will receive notification 15 min before ride start."));
    }

    @Test
    @DisplayName("2.4.1 izuzetak: putnik već ima aktivnu voznju.")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_passengerHasActiveRide_returns400() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();

        when(rideService.createRide(any(), eq("passenger@test.com")))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "You already have an active ride"));

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.4.1 izuzetak: nema dostupnih vozaca.")
    @WithMockUser(username = "passenger@test.com", roles = {"PASSENGER"})
    void createRide_noDriversAvailable_returns400() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();

        when(rideService.createRide(any(), eq("passenger@test.com")))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "No available drivers"));

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.4.1 izuzetak: neautentifikovani korisnik pokušava da kreira vožnju -> 401")
    void createRide_unauthenticated_returns401or403() throws Exception {
        CreateRideRequestDTO request = buildValidCreateRideRequest();

        mockMvc.perform(post("/api/v1/rides/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private CreateRideRequestDTO buildValidCreateRideRequest() {
        CreateRideRequestDTO request = new CreateRideRequestDTO();
        request.setLocations(List.of(
                new RideStopDTO(19.83, 45.25, "Polaziste"),
                new RideStopDTO(19.85, 45.26, "Odrediste")
        ));
        request.setVehicleType(VehicleType.STANDARD);
        request.setDistanceKm(5.0);
        request.setEstimatedTime(10.0);
        request.setBabyTransport(false);
        request.setPetTransport(false);
        return request;
    }
}
