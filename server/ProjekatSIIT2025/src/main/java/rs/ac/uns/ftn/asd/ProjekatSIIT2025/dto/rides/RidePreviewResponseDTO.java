package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RidePreviewResponseDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalPrice;
    private Long riderId;
    private ArrayList<Long> passengers;
    private ArrayList<Long> paths;
    private Long rideRejectionId;
    private boolean panic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public ArrayList<Long> getPassengers() {
        return passengers;
    }

    public void setPassengers(ArrayList<Long> passengers) {
        this.passengers = passengers;
    }

    public ArrayList<Long> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<Long> paths) {
        this.paths = paths;
    }

    public Long getRideRejectionId() {
        return rideRejectionId;
    }

    public void setRideRejectionId(Long rideRejectionId) {
        this.rideRejectionId = rideRejectionId;
    }

    public boolean isPanic() {
        return panic;
    }

    public void setPanic(boolean panic) {
        this.panic = panic;
    }
}
