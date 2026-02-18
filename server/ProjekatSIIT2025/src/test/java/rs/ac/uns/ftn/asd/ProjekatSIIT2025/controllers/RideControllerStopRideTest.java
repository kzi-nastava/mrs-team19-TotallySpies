package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.StopRideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStop;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.RideService;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RideControllerStopRideTest {

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

    private StopRideDTO validDto() {
        RideStop newDestination = new RideStop(
                1L,
                "Trg slobode 1, Novi Sad",
                45.2551,
                19.8452,
                1,
                null
        );

        return new StopRideDTO(
                1L,
                LocalDateTime.of(2026, 2, 5, 12, 30),
                950.0,
                newDestination
        );
    }
    @Test
    @DisplayName("DRIVER stops ride -> 200")
    @WithMockUser(username = "driver@mail.com", roles = "DRIVER")
    void stopRide_driver_ok() throws Exception {
        doNothing().when(rideService).stopRide(ArgumentMatchers.any(StopRideDTO.class));
        mockMvc.perform(
                        put("/api/v1/rides/stop-ride")
                                .contentType(MediaType.APPLICATION_JSON)
                                //transform java to json and send as http request body
                                .content(om.writeValueAsString(validDto()))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Ride stopped!"));

        //checks if the service is called in controller
        verify(rideService).stopRide(ArgumentMatchers.any(StopRideDTO.class));
    }
    @Test
    @DisplayName("Security test: unauthorized user stops the ride -> 401")
    void stopRide_unauthorizedUser_should_be_rejected() throws Exception{
        mockMvc.perform(
                put("/api/v1/rides/stop-ride")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsString(validDto()))
                )
                .andExpect(status().is4xxClientError());
    }
    @Test
    @DisplayName("Security test: passenger stops the ride -> 403")
    @WithMockUser(username = "passenger@mail.com", roles = "PASSENGER")
    void stopRide_notDriver_forbidden() throws Exception {
        mockMvc.perform(
                        put("/api/v1/rides/stop-ride")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(validDto()))
                )
                .andExpect(status().isForbidden());
    }
    @Test
    @DisplayName("Exception: Ride not found -> 404")
    @WithMockUser(username = "driver@mail.com", roles = "DRIVER")
    void stopRide_rideNotFound_notFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found!"))
                .when(rideService).stopRide(ArgumentMatchers.any(StopRideDTO.class));
        mockMvc.perform(
                        put("/api/v1/rides/stop-ride")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(validDto()))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Exception: User not found -> 404")
    @WithMockUser(username = "driver@mail.com", roles = "DRIVER")
    void stopRide_userNotFound_notFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"))
                .when(rideService).stopRide(ArgumentMatchers.any(StopRideDTO.class));
        mockMvc.perform(
                        put("/api/v1/rides/stop-ride")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(validDto()))
                )
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("Exception: End time null -> 400")
    @WithMockUser(username = "driver@mail.com", roles = "DRIVER")
    void stopRide_endTimeNull_400() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time can not be null!"))
                .when(rideService).stopRide(ArgumentMatchers.any(StopRideDTO.class));
        StopRideDTO dto = new StopRideDTO(
                1L,
                null, // null end time
                950.0,
                new RideStop(1L, "Trg slobode 1, Novi Sad", 45.2551, 19.8452, 1, null)
        );
        mockMvc.perform(
                        put("/api/v1/rides/stop-ride")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    //if service throws bad request exception, endpoint should return status code 400
    @DisplayName("Exception: Ride has no stops -> 400")
    @WithMockUser(username = "driver@mail.com", roles = "DRIVER")
    void stopRide_noStops_badRequest() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride has no stops"))
                .when(rideService).stopRide(ArgumentMatchers.any(StopRideDTO.class));
        mockMvc.perform(
                        put("/api/v1/rides/stop-ride")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(validDto()))
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    //test if endpoint accepts empty request
    @DisplayName("REST: missing body -> 400")
    @WithMockUser(username = "driver@mail.com", roles = "DRIVER")
    void stopRide_missingBody_badRequest() throws Exception {
        mockMvc.perform(
                        put("/api/v1/rides/stop-ride")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}
