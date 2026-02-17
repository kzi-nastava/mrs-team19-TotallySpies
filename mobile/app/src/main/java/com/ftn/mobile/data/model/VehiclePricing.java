package com.ftn.mobile.data.model;

public class VehiclePricing {
    private VehicleType vehicleType;
    private double basePrice;

    public VehiclePricing() {
    }

    public VehiclePricing(VehicleType vehicleType, double basePrice) {
        this.vehicleType = vehicleType;
        this.basePrice = basePrice;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
}
