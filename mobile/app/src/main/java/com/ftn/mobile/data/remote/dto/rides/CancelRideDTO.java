package com.ftn.mobile.data.remote.dto.rides;

import java.time.LocalDateTime;

public class CancelRideDTO {
    private Long rideId;
    private String rejectionReason;
    //private LocalDateTime time;
    private String time;

    public CancelRideDTO(Long rideId, String rejectionReason, String time) {
        this.rideId = rideId;
        this.rejectionReason = rejectionReason;
        this.time = time;
    }

    public Long getRideId() {
        return rideId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getTime() {
        return time;
    }
}
