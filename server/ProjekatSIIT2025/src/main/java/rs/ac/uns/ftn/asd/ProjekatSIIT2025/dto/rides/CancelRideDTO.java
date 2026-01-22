package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import java.time.LocalDateTime;

public class CancelRideDTO {
    private Long rideId;
    private Long userId;
    private String rejectionReason;
    private LocalDateTime time;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
