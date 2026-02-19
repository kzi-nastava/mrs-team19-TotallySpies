package com.ftn.mobile.data.remote.dto.rides;

public class PanicRideDTO {
    private Long rideId;
    private String reason;

    public PanicRideDTO(Long rideId, String reason) {
        this.rideId = rideId;
        this.reason = reason;
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
}
