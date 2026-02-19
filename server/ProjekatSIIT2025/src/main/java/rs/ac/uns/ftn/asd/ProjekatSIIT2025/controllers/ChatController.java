package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.ChatMessageDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ChatMessage;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ChatMessageRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.UserService;

@RestController
@RequestMapping("api/v1/chat")
public class ChatController {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserService userService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findBySenderIdOrReceiverIdOrderByTimestampAsc(userId, userId);
        return ResponseEntity.ok(messages);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO dto) throws Exception {
        System.out.println("sendMessage called with DTO: " + dto);
        
        try {
            // Get sender ID directly from DTO
            if (dto.getSenderId() == null) {
                System.err.println("Sender ID is null in DTO!");
                return;
            }
            
            User sender = userService.findById(dto.getSenderId());
            System.out.println("Sender found: " + sender.getEmail());

            ChatMessage message = new ChatMessage();
            message.setSenderId(sender.getId());
            message.setReceiverId(null);
            message.setContent(dto.getContent());
            message.setTimestamp(LocalDateTime.now());
            
            ChatMessage saved = chatMessageRepository.save(message);
            System.out.println("Message saved with ID: " + saved.getId());

            messagingTemplate.convertAndSend("/topic/admin/messages", saved);
            System.out.println("Message sent to /topic/admin/messages");
            messagingTemplate.convertAndSend("/topic/user/" + sender.getId(), saved);
            
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat.adminResponse")
    public void adminResponse(@Payload ChatMessageDTO dto) throws Exception {
        try {
            if (dto.getSenderId() == null || dto.getReceiverId() == null) {
                System.err.println("Sender or receiver ID is null!");
                return;
            }
            
            User admin = userService.findById(dto.getSenderId());
            
            ChatMessage message = new ChatMessage();
            message.setSenderId(admin.getId());
            message.setReceiverId(dto.getReceiverId());
            message.setContent(dto.getContent());
            message.setTimestamp(LocalDateTime.now());

            ChatMessage saved = chatMessageRepository.save(message);
            
            messagingTemplate.convertAndSend("/topic/user/" + dto.getReceiverId(), saved);
            messagingTemplate.convertAndSend("/topic/admin/messages", saved);
            
        } catch (Exception e) {
            System.err.println("Error sending admin response: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getChatUsers() {
        // Find all unique senderIds where receiverId is null (sent to admin)
        List<Long> userIds = chatMessageRepository.findDistinctSenderIdsByReceiverIdIsNull();
        List<User> users = userService.findAllById(userIds);
        List<UserDTO> dtos = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail(), user.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

}