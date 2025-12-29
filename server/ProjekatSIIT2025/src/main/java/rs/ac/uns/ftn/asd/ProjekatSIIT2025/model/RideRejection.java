package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import java.time.LocalDateTime;

public class RideRejection {
    private Long id;
    private Long userId;
    private Long rideId;
    private String rejectionReason;
    private LocalDateTime time;

    public RideRejection(Long id, Long userId, Long rideId, String rejectionReason, LocalDateTime time) {
        this.id = id;
        this.userId = userId;
        this.rideId = rideId;
        this.rejectionReason = rejectionReason;
        this.time = time;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
