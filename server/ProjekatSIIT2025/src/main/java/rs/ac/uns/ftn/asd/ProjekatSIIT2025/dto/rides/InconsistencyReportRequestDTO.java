package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InconsistencyReportRequestDTO {
    @NotNull(message = "ID must be provided")
    private Long rideId;
    @NotBlank(message = "description cannot be empty")
    @Size(max = 500, message = "max 500 chars")
    private String description;

    public Long getRideId() {
        return rideId;
    }
    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    
}
