package rs.ac.uns.ftn.asd.ProjekatSIIT2025.websockets.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideTrackingDTO;

@Service
public class RedisReceiver {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    public void receiveVehicleMessage(String message) {
        // when message arrives from redis we push it into websocket topic
        messagingTemplate.convertAndSend("/topic/map-updates", message);
    }

    public void receiveRideMessage(String message) {
        try {
            RideTrackingDTO dto = objectMapper.readValue(message, RideTrackingDTO.class);
            messagingTemplate.convertAndSend("/topic/ride/" + dto.getRideId(), dto);
        } catch (Exception e) {
            
        }
    }
}