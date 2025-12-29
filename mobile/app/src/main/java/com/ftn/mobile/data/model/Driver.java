package com.ftn.mobile.data.model;

public class Driver {
    User user;
    private boolean isActive;
    private CarInfo carInfo;

    public Driver(User user, CarInfo carInfo) {
        this.user = user;
        this.isActive = isActive;
        this.carInfo = carInfo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public CarInfo getCarInfo() {
        return carInfo;
    }

    public void setCarInfo(CarInfo carInfo) {
        this.carInfo = carInfo;
    }
}
