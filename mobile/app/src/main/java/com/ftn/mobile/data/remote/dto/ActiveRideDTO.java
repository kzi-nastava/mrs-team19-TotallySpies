package com.ftn.mobile.data.remote.dto;

import java.util.List;

public class ActiveRideDTO {
    private Long id;
    private Long driverId;
    private String driverName;
    private String vehicleModel;
    private List<String> passengers;
    private String startLocation;
    private String endLocation;
    private double price;
    private String startTime;
    private boolean panicPressed;

    public ActiveRideDTO() {
    }

    public ActiveRideDTO(Long id, Long driverId, boolean panicPressed, String startTime, double price, String endLocation, String startLocation, List<String> passengers, String vehicleModel, String driverName) {
        this.id = id;
        this.driverId = driverId;
        this.panicPressed = panicPressed;
        this.startTime = startTime;
        this.price = price;
        this.endLocation = endLocation;
        this.startLocation = startLocation;
        this.passengers = passengers;
        this.vehicleModel = vehicleModel;
        this.driverName = driverName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isPanicPressed() {
        return panicPressed;
    }

    public void setPanicPressed(boolean panicPressed) {
        this.panicPressed = panicPressed;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
}