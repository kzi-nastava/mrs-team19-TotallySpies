package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class InconsistencyReportRequestDTO {
    private Long rideId;
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
