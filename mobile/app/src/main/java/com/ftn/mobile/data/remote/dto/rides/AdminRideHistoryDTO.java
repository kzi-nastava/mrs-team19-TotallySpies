package com.ftn.mobile.data.remote.dto.rides;

import java.time.LocalDateTime;
import java.util.List;

public class AdminRideHistoryDTO {
    private Long rideId;
    private List<RideStopDTO> rideStops;
    private String startedAt;
    private String finishedAt;
    private String createdAt;
    private double totalPrice;
    private String userWhoCancelled;
    private boolean cancelled;
    private boolean panic;

    public AdminRideHistoryDTO(Long rideId, List<RideStopDTO> rideStops, String startedAt, String finishedAt, String createdAt, double totalPrice, String userWhoCancelled, boolean cancelled, boolean panic) {
        this.rideId = rideId;
        this.rideStops = rideStops;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
        this.userWhoCancelled = userWhoCancelled;
        this.cancelled = cancelled;
        this.panic = panic;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUserWhoCancelled() {
        return userWhoCancelled;
    }

    public void setUserWhoCancelled(String userWhoCancelled) {
        this.userWhoCancelled = userWhoCancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isPanic() {
        return panic;
    }

    public void setPanic(boolean panic) {
        this.panic = panic;
    }
}
