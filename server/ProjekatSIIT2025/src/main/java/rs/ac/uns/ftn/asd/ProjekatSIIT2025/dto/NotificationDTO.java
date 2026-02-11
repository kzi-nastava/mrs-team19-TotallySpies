package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private Long rideId; // nullable
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationDTO() {}

    public NotificationDTO(Long id, Long rideId, String message, String type, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.rideId = rideId;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
