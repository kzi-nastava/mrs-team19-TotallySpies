package com.ftn.mobile.data.remote.dto;

import com.ftn.mobile.data.model.RideStatus;

import java.util.List;

public class PassengerUpcomingRideDTO {
    Long rideId;
    RideStatus status;
    double price;
    String driverName;
    List<RideStopDTO> locations;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public List<RideStopDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RideStopDTO> locations) {
        this.locations = locations;
    }
}
