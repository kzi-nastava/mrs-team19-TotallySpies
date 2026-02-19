package com.ftn.mobile.data.remote.dto.rides;

import java.util.List;

public class PassengerRideHistoryDTO {
    private Long rideId;
    private List<RideStopDTO> rideStops;
    private String startedAt;
    private String finishedAt;
    private String createdAt;

    public PassengerRideHistoryDTO(Long rideId, List<RideStopDTO> rideStops, String startedAt, String finishedAt, String createdAt) {
        this.rideId = rideId;
        this.rideStops = rideStops;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.createdAt = createdAt;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public List<RideStopDTO> getRideStops() {
        return rideStops;
    }

    public void setRideStops(List<RideStopDTO> rideStops) {
        this.rideStops = rideStops;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(String finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
