package com.ftn.mobile.data.remote.dto;

public class RideStopDTO {
    private String address;
    private Double lat;
    private Double lng;
    private int orderIndex;

    public RideStopDTO(Double latitude, Double longitude, String address) {
        this.address = address;
        this.lat = latitude;
        this.lng = longitude;
    }

    public RideStopDTO() {}

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
