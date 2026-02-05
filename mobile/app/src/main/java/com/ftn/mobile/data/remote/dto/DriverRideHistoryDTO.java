package com.ftn.mobile.data.remote.dto;

import java.util.List;

public class DriverRideHistoryDTO {
    private Long id;
    private String startTime;
    private String endTime;
    private double price;
    private List<String> passengers;
    private String startLocation;
    private String endLocation;
    private boolean cancelled;
    private String cancelledBy;
    private String cancellReason;
    private boolean panicPressed;
    private String panicReason;

    public DriverRideHistoryDTO() {
    }

    public DriverRideHistoryDTO(Long id, String startTime, String endTime, double price, List<String> passengers, String startLocation, String endLocation, boolean cancelled, String cancelledBy, String cancellReason, boolean panicPressed, String panicReason) {
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
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
