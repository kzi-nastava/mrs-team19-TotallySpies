package com.ftn.mobile.data.remote.dto;

public class InconsistencyReportRequestDTO {
    Long rideId;
    String description;

    public InconsistencyReportRequestDTO(Long rideId, String description) {
        this.rideId = rideId;
        this.description = description;
    }

    public InconsistencyReportRequestDTO() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
