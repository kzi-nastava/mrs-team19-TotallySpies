package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RideDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalPrice;
    private Long riderId;
    private ArrayList<Long> passengers;
    private ArrayList<Long> paths;
    private ArrayList<Long> reviews;
    private RideStatus status;
    private Long rideRejectionId;
    private boolean panic;
    private boolean babiesTransport;
    private boolean petsTransport;
    private VehicleType vehicleType;
    private ArrayList<Long> reports;

    public RideDTO() {}

    public RideDTO(Long id, LocalDateTime startTime, LocalDateTime endTime, double totalPrice,
                   Long riderId, ArrayList<Long> passengers, ArrayList<Long> paths, ArrayList<Long> reviews,
                   RideStatus status, Long rideRejectionId, boolean panic, boolean babiesTransport, boolean petsTransport,
                   VehicleType vehicleType, ArrayList<Long> reports) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.riderId = riderId;
        this.passengers = passengers;
        this.paths = paths;
        this.reviews = reviews;
        this.status = status;
        this.rideRejectionId = rideRejectionId;
        this.panic = panic;
        this.babiesTransport = babiesTransport;
        this.petsTransport = petsTransport;
        this.vehicleType = vehicleType;
        this.reports = reports;
    }

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

    public ArrayList<Long> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Long> reviews) {
        this.reviews = reviews;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
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

    public boolean isBabiesTransport() {
        return babiesTransport;
    }

    public void setBabiesTransport(boolean babiesTransport) {
        this.babiesTransport = babiesTransport;
    }

    public boolean isPetsTransport() {
        return petsTransport;
    }

    public void setPetsTransport(boolean petsTransport) {
        this.petsTransport = petsTransport;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public ArrayList<Long> getReports() {
        return reports;
    }

    public void setReports(ArrayList<Long> reports) {
        this.reports = reports;
    }
}
