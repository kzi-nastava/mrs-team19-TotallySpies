package com.ftn.mobile.data.remote.dto;

public class InconsistencyReportRequestDTO {
    Long id;
    String description;

    public InconsistencyReportRequestDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
