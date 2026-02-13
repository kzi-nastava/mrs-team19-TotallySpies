package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideCancellation;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStop;

import java.time.LocalDateTime;
import java.util.List;

public class AdminRideHistoryResponseDTO {
    private Long rideId;
    private List<RideStop> rideStops;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private double totalPrice;
    private String userWhoCancelled;
    private boolean isCancelled;
    private boolean panic;

    public AdminRideHistoryResponseDTO(LocalDateTime finishedAt, LocalDateTime startedAt, LocalDateTime createdAt,
                                       List<RideStop> stops, Long id, double totalPrice, boolean panic,
                                       String userWhoCancelled, boolean isCancelled) {
        this.finishedAt = finishedAt;
        this.startedAt = startedAt;
        this.createdAt = createdAt;
        this.rideStops = stops;
        this.rideId = id;
        this.totalPrice = totalPrice;
        this.panic = panic;
        this.userWhoCancelled = userWhoCancelled;
        this.isCancelled = isCancelled;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public List<RideStop> getRideStops() {
        return rideStops;
    }

    public void setRideStops(List<RideStop> rideStops) {
        this.rideStops = rideStops;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
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
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean getPanic() {
        return panic;
    }

    public void setPanic(boolean panic) {
        this.panic = panic;
    }
}
