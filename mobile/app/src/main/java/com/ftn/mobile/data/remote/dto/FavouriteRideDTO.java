package com.ftn.mobile.data.remote.dto;

import java.util.Comparator;
import java.util.List;

public class FavouriteRideDTO {
    private Long id;
    private List<RideStopDTO> locations;

    public FavouriteRideDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RideStopDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RideStopDTO> locations) {
        this.locations = locations;
    }
}
