package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import java.time.LocalDateTime;
import java.util.List;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

public class ActiveRideDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private List<String> passengers;
    private String startLocation;  
    private String endLocation;
    
    private Long driverId;
    private String driverName;
    private String driverEmail;
    private String driverPicture;
    private String vehicleModel;
    private double driverAverageRating;

    private boolean panicPressed;
    private String panicReason;
    private RideStatus status;
    
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
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public List<String> getPassengers() {
        return passengers;
    }
    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
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
    public Long getDriverId() {
        return driverId;
    }
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
    public String getDriverName() {
        return driverName;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public String getDriverEmail() {
        return driverEmail;
    }
    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }
    public String getDriverPicture() {
        return driverPicture;
    }
    public void setDriverPicture(String driverPicture) {
        this.driverPicture = driverPicture;
    }
    public String getVehicleModel() {
        return vehicleModel;
    }
    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }
    public double getDriverAverageRating() {
        return driverAverageRating;
    }
    public void setDriverAverageRating(double driverAverageRating) {
        this.driverAverageRating = driverAverageRating;
    }
    public boolean isPanicPressed() {
        return panicPressed;
    }
    public void setPanicPressed(boolean panicPressed) {
        this.panicPressed = panicPressed;
    }
    public String getPanicReason() {
        return panicReason;
    }
    public void setPanicReason(String panicReason) {
        this.panicReason = panicReason;
    }
    public RideStatus getStatus() {
        return status;
    }
    public void setStatus(RideStatus status) {
        this.status = status;
    }

    
}