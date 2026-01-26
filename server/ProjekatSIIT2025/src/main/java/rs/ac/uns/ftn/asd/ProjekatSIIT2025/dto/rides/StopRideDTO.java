package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStop;

import java.time.LocalDateTime;

public class StopRideDTO {
    private Long userId;
    private Long rideId;
    private LocalDateTime newEndTime;
    private double newTotalPrice;
    private RideStop newDestination;

    public StopRideDTO(Long userId, Long rideId, LocalDateTime newEndTime, double newTotalPrice, RideStop newDestination) {
        this.userId = userId;
        this.rideId = rideId;
        this.newEndTime = newEndTime;
        this.newTotalPrice = newTotalPrice;
        this.newDestination = newDestination;
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

    public LocalDateTime getNewEndTime() {
        return newEndTime;
    }

    public void setNewEndTime(LocalDateTime newEndTime) {
        this.newEndTime = newEndTime;
    }

    public double getNewTotalPrice() {
        return newTotalPrice;
    }

    public void setNewTotalPrice(double newTotalPrice) {
        this.newTotalPrice = newTotalPrice;
    }

    public RideStop getNewDestination() {
        return newDestination;
    }

    public void setNewDestination(RideStop newDestination) {
        this.newDestination = newDestination;
    }
}
