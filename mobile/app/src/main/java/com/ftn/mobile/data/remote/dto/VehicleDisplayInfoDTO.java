package com.ftn.mobile.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class VehicleDisplayInfoDTO {
    private Long id;
    private double currentLat;
    private double currentLng;
    @SerializedName("busy")
    private boolean isBusy;

    public VehicleDisplayInfoDTO() {
    }

    public VehicleDisplayInfoDTO(Long id, boolean isBusy, double currentLng, double currentLat) {
        this.id = id;
        this.isBusy = isBusy;
        this.currentLng = currentLng;
        this.currentLat = currentLat;
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
}
