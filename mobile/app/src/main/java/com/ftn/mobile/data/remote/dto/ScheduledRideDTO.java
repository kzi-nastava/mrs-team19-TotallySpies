package com.ftn.mobile.data.remote.dto;

import com.ftn.mobile.data.model.RideStatus;

import java.util.List;

public class ScheduledRideDTO {
    private Long rideId;
    private String finishTime;
    private RideStatus status;
    private double totalPrice;
    private Long nextRideId;
    private String displayTime;
    private String startLocation;
    private String endLocation;
    private List<UserProfileResponseDTO> passengers;

    public ScheduledRideDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public List<UserProfileResponseDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<UserProfileResponseDTO> passengers) {
        this.passengers = passengers;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public Long getNextRideId() {
        return nextRideId;
    }

    public void setNextRideId(Long nextRideId) {
        this.nextRideId = nextRideId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }
}
