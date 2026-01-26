package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import java.time.LocalDateTime;
import java.util.List;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;

public class DriverRideHistoryResponseDTO {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private List<String> passengers;
    private String startLocation;  
    private String endLocation;
    private boolean cancelled;
    private String cancelledBy;
    private String cancellReason;
    private boolean panicPressed;
    private String panicReason;
    
    public DriverRideHistoryResponseDTO() {
    }
    
    public DriverRideHistoryResponseDTO(Long id, LocalDateTime startTime, LocalDateTime endTime, double price,
            List<String> passengers, String startLocation, String endLocation, boolean cancelled, String cancelledBy,
            String cancellReason, boolean panicPressed, String panicReason) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.passengers = passengers;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.cancelled = cancelled;
        this.cancelledBy = cancelledBy;
        this.cancellReason = cancellReason;
        this.panicPressed = panicPressed;
        this.panicReason = panicReason;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancellReason() {
        return cancellReason;
    }

    public void setCancellReason(String cancellReason) {
        this.cancellReason = cancellReason;
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

    
}