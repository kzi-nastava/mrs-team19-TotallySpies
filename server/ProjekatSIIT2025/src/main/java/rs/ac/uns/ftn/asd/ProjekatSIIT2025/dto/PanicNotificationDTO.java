package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto;

public class PanicNotificationDTO {
    private Long rideId;
    private String reason;
    private String userEmail;

    public PanicNotificationDTO(Long rideId, String reason, String userEmail) {
        this.rideId = rideId;
        this.reason = reason;
        this.userEmail = userEmail;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
