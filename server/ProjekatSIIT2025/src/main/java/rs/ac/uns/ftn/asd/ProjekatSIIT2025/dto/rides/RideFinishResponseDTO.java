package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import java.time.LocalDateTime;
import java.util.List;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

public class RideFinishResponseDTO {
    private Long rideId;
    private Double totalPrice;
    private LocalDateTime displayTime;
    private LocalDateTime finishTime;
    private RideStatus status;
    private Long nextRideId;
    private String startLocation;
    private String endLocation;
    private List<UserProfileResponseDTO> passengers;

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

    public RideStatus getStatus() {
        return status;
    }

        public LocalDateTime getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(LocalDateTime displayTime) {
        this.displayTime = displayTime;
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

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public List<UserProfileResponseDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<UserProfileResponseDTO> passengers) {
        this.passengers = passengers;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }    
    
}
