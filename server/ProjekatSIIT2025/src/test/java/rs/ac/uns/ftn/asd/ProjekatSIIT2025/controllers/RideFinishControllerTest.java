package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideFinishResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.RideService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RideFinishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RideService rideService;

    private ObjectMapper om;

    @BeforeEach
    void setup() {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
    }


    @Test
    @DisplayName("2.7.1 driver finishes ride -> 200 OK")
    @WithMockUser(username = "driver@gmail.com", roles = "DRIVER")
    void finishRide_driver_ok() throws Exception {
        Long rideId = 1L;
        RideFinishResponseDTO response = new RideFinishResponseDTO();
        response.setRideId(rideId);
        response.setStatus(RideStatus.COMPLETED);

        when(rideService.finishRide(rideId)).thenReturn(response);

        mockMvc.perform(put("/api/v1/rides/{id}/end", rideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.rideId").value(rideId));

        verify(rideService).finishRide(rideId);
    }

    @Test
    @DisplayName("2.7.2 - not logged in user -> 403")
    void finishRide_unauthorized_401() throws Exception {
        mockMvc.perform(put("/api/v1/rides/1/end")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("2.7.3 - passenger tries to finish ride -> 403")
    @WithMockUser(username = "passenger@gmail.com", roles = "PASSENGER")
    void finishRide_wrongRole_403() throws Exception {
        mockMvc.perform(put("/api/v1/rides/1/end")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("2.7.4 - ride not found -> 404")
    @WithMockUser(username = "driver@gmail.com", roles = "DRIVER")
    void finishRide_notFound_404() throws Exception {
        when(rideService.finishRide(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found"));

        mockMvc.perform(put("/api/v1/rides/999/end")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("2.7.5 - invalid ID format (string instead of long) -> 400")
    @WithMockUser(username = "driver@gmail.com", roles = "DRIVER")
    void finishRide_badFormat_400() throws Exception {
        mockMvc.perform(put("/api/v1/rides/invalid-id/end")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2.7.6 - DRIVER gets scheduled rides -> 200 OK")
    @WithMockUser(username = "driver@gmail.com", roles = "DRIVER")
    void getFutureRides_ok() throws Exception {
        List<RideFinishResponseDTO> rides = new ArrayList<>();
        RideFinishResponseDTO r1 = new RideFinishResponseDTO();
        r1.setRideId(10L);
        rides.add(r1);

        when(rideService.findScheduledRides()).thenReturn(rides);

        mockMvc.perform(get("/api/v1/rides/scheduled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].rideId").value(10L));
    }

    @Test
    @DisplayName("2.7.7 - wrong HTTP Method POST instead of GET -> 405")
    @WithMockUser(username = "driver@mail.com", roles = "DRIVER")
    void getFutureRides_wrongMethod_405() throws Exception {
        mockMvc.perform(post("/api/v1/rides/scheduled"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("2.7.8 - driver tries to finish another driver's ride -> 403")
    @WithMockUser(username = "other.driver@gmail.com", roles = "DRIVER")
    void finishRide_WrongDriver_403() throws Exception {
        Long rideId = 1L;
        when(rideService.finishRide(rideId))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the driver of this ride"));

        mockMvc.perform(put("/api/v1/rides/{id}/end", rideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("2.7.9 - driver tries to finish ride that is already completed -> 200 (ili 400 ako je zabranjeno)")
    @WithMockUser(username = "driver@gmail.com", roles = "DRIVER")
    void finishRide_AlreadyCompleted_StillOk() throws Exception {
        Long rideId = 1L;
        RideFinishResponseDTO response = new RideFinishResponseDTO();
        response.setRideId(rideId);
        response.setStatus(RideStatus.COMPLETED);

        when(rideService.finishRide(rideId)).thenReturn(response);

        mockMvc.perform(put("/api/v1/rides/{id}/end", rideId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}