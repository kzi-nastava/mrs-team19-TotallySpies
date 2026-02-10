package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStop;

import java.time.LocalDateTime;
import java.util.List;

public class PassengerRideHistoryResponseDTO {
    private Long rideId;
    private List<RideStop> rideStops;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;

    public PassengerRideHistoryResponseDTO(LocalDateTime finishedAt, LocalDateTime startedAt, LocalDateTime createdAt, List<RideStop> rideStops, Long rideId) {
        this.finishedAt = finishedAt;
        this.startedAt = startedAt;
        this.createdAt = createdAt;
        this.rideStops = rideStops;
        this.rideId = rideId;
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
}
