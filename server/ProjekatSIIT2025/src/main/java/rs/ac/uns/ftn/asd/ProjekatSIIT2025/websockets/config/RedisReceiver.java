package rs.ac.uns.ftn.asd.ProjekatSIIT2025.websockets.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisReceiver {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void receiveMessage(String message) {
        // when message arrives from redis we push it into websocket topic
        messagingTemplate.convertAndSend("/topic/map-updates", message);
    }
}