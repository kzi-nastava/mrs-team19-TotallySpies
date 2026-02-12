package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.NotificationDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.NotificationService;

import java.util.List;

@RestController
@RequestMapping("api/v1/notifications")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping(value = "")
    public ResponseEntity<List<NotificationDTO>> getNotifications(Authentication auth) {
        String email = auth.getName();

        List<NotificationDTO> notifications = notificationService.getNotifications(email);

        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}
