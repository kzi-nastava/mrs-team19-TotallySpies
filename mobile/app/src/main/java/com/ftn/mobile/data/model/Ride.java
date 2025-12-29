package com.ftn.mobile.data.model;

import java.util.List;

public class Ride {
    private String departure;
    private String destination;
    private String price;
    private String dateStart;
    private String dateEnd;
    private String startTime;
    private String endTime;
    private boolean isPanic;
    private RideStatus status;
    private List<String> passengers;
    private List<Integer> passengersImages;

    public Ride() {
    }

    public Ride(String departure, String destination, String price, String dateStart,
                String dateEnd, String startTime, String endTime, boolean isPanic,
                RideStatus status, List<String> passengers, List<Integer> passengersImages) {
        this.departure = departure;
        this.destination = destination;
        this.price = price;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isPanic = isPanic;
        this.status = status;
        this.passengers = passengers;
        this.passengersImages = passengersImages;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
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

    public boolean isPanic() {
        return isPanic;
    }

    public void setPanic(boolean panic) {
        isPanic = panic;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public List<Integer> getPassengersImages() {
        return passengersImages;
    }

    public void setPassengersImages(List<Integer> passengersImages) {
        this.passengersImages = passengersImages;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "departure='" + departure + '\'' +
                ", destination='" + destination + '\'' +
                ", price='" + price + '\'' +
                ", dateStart='" + dateStart + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", isPanic=" + isPanic +
                ", status=" + status +
                ", passengers=" + passengers +
                ", passengersImages=" + passengersImages +
                '}';
    }
}