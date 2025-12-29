package com.ftn.mobile.data.model;

public class CarInfo {
    public String model, type, plates, seats;
    public boolean isPetFriendly, isBabyFriendly;
    public CarInfo(String model, String type, String plates, String seats, boolean isPet, boolean isBaby) {
        this.model = model; this.type = type; this.plates = plates;
        this.seats = seats; this.isPetFriendly = isPet; this.isBabyFriendly = isBaby;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlates() {
        return plates;
    }

    public void setPlates(String plates) {
        this.plates = plates;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public boolean isPetFriendly() {
        return isPetFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        isPetFriendly = petFriendly;
    }

    public boolean isBabyFriendly() {
        return isBabyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        isBabyFriendly = babyFriendly;
    }
}
