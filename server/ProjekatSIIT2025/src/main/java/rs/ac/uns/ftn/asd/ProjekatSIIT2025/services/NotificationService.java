package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.NotificationDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Notification;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.NotificationType;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.NotificationRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    public List<NotificationDTO> getNotifications(String userEmail){
        User user = userRepository.findByEmail(userEmail);
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<Notification> notificationsByUser = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        List<NotificationDTO> notificationDTOList = new ArrayList<>();
        for (Notification notification : notificationsByUser){
            notificationDTOList.add(new NotificationDTO(notification.getId(), notification.getRide().getId(),
                    notification.getMessage(), notification.getType().toString(),
                    notification.isRead(), notification.getCreatedAt()));
        }

        return notificationDTOList;
    }

    public void notifyUser(User user, Ride ride, String message, NotificationType type) {
        // sacuvaj u bazi
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setRide(ride);
        notification.setMessage(message);
        notification.setType(type);
        notificationRepository.save(notification);

        // salji putem websocket-a
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + user.getId(),
                new NotificationDTO(
                        notification.getId(),
                        ride != null ? ride.getId() : null,
                        message,
                        type.name(),
                        false,
                        notification.getCreatedAt()
                )
        );
    }

    public void markAsRead(Long id) {
        Notification notification;
        try {
            notification = notificationRepository.findById(id)
                .orElseThrow(() -> new Exception("Notification not found with id: " + id));
        notification.setRead(true);
        notificationRepository.save(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
