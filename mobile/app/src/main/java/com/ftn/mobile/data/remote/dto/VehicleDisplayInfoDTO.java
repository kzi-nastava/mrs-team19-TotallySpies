package com.ftn.mobile.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class VehicleDisplayInfoDTO {
    private Long id;
    private Long driverId;
    private double currentLat;
    private double currentLng;
    private String driverName;
    @SerializedName("busy")
    private boolean isBusy;

    public VehicleDisplayInfoDTO() {
    }

    public VehicleDisplayInfoDTO(Long id, Long driverId, boolean isBusy, double currentLng, double currentLat, String driverName) {
        this.id = id;
        this.driverId = driverId;
        this.isBusy = isBusy;
        this.currentLng = currentLng;
        this.currentLat = currentLat;
        this.driverName = driverName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public String getDriverName() {
        return driverName;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
}
