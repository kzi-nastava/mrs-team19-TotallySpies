package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import java.time.LocalDateTime;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

public class RideFinishResponseDTO {
    private Long rideId;
    private Double totalPrice;
    private LocalDateTime finishTime;
    private RideStatus status;
    private Long nextRideId;

    public RideFinishResponseDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Long getNextRideId() {
        return nextRideId;
    }

    public void setNextRideId(Long nextRideId) {
        this.nextRideId = nextRideId;
    }
    
}
