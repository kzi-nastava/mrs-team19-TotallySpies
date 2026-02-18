package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

public class CreateRideRequestDTO {
    @NotNull(message = "Locations list cannot be null")
    @Size(min = 2, message = "At least two locations are required (departure and destination)")
    private List<RideStopDTO> locations; // lista stanica sa koordinatama

    private List<String> passengerEmails; // emailovi ostalih putnika

    @NotNull(message = "Vehicle type must be specified")
    private VehicleType vehicleType;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private Double distanceKm;

    @NotNull(message = "Estimated time is required")
    @Positive(message = "Estimated time must be positive")
    private Double estimatedTime;

    private boolean babyTransport;
    private boolean petTransport;
    private LocalDateTime scheduledFor;

    private List<RoutePointDTO> path;

    public List<RideStopDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RideStopDTO> locations) {
        this.locations = locations;
    }

    public List<String> getPassengerEmails() {
        return passengerEmails;
    }

    public void setPassengerEmails(List<String> passengerEmails) {
        this.passengerEmails = passengerEmails;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabyTransport() {
        return babyTransport;
    }

    public void setBabyTransport(boolean babyTransport) {
        this.babyTransport = babyTransport;
    }

    public boolean isPetTransport() {
        return petTransport;
    }

    public void setPetTransport(boolean petTransport) {
        this.petTransport = petTransport;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(Double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public List<RoutePointDTO> getPath() {
        return path;
    }

    public void setPath(List<RoutePointDTO> path) {
        this.path = path;
    }
}
