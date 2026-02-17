package com.ftn.mobile.data.remote.dto;

import com.ftn.mobile.data.model.VehicleType;

public class UpdatePriceDTO {
    private VehicleType vehicleType;
    private double newPrice;

    public UpdatePriceDTO(VehicleType vehicleType, double newPrice) {
        this.vehicleType = vehicleType;
        this.newPrice = newPrice;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }
}
