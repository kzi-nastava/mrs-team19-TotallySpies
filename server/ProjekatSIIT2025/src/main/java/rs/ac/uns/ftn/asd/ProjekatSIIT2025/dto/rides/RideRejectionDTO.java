package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import java.time.LocalDateTime;

public class RideRejectionDTO {
    private Long userId;
    private Long rideId;
    private String rejectionReason;
    private LocalDateTime time;

    public RideRejectionDTO() {}

    public RideRejectionDTO(Long userId, Long rideId, String rejectionReason, LocalDateTime time) {
        this.userId = userId;
        this.rideId = rideId;
        this.rejectionReason = rejectionReason;
        this.time = time;
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
