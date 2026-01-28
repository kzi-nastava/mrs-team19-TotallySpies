package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import java.time.LocalDateTime;

public class PanicNotificationDTO {
    private Long rideId;
    LocalDateTime time;
    String reason;

    public PanicNotificationDTO( Long rideId, LocalDateTime time, String reason) {
        this.rideId = rideId;
        this.time = time;
        this.reason = reason;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
