package com.ftn.mobile.data.remote.dto;

import com.ftn.mobile.data.model.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

public class CreateRideRequestDTO {
    private List<RideStopDTO> locations;
    private List<String> passengerEmails;
    private VehicleType vehicleType;
    private Double distanceKm;
    private Double estimatedTime;
    private boolean babyTransport;
    private boolean petTransport;
    private LocalDateTime scheduledFor;

    private List<RoutePointDTO> path;

    public List<RideStopDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RideStopDTO> locations) {
        this.locations = locations;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabyTransport() {
        return babyTransport;
    }

    public void setBabyTransport(boolean babyTransport) {
        this.babyTransport = babyTransport;
    }

    public boolean isPetTransport() {
        return petTransport;
    }

    public void setPetTransport(boolean petTransport) {
        this.petTransport = petTransport;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public List<RoutePointDTO> getPath() {
        return path;
    }

    public void setPath(List<RoutePointDTO> path) {
        this.path = path;
    }
}
