package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Path {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startingAddress;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dest_location_id", nullable = false)
    private Location destinationAddress;
    private double mileage;
    private double estimatedTime;
    private double pricePerMileage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="passenger_id")
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ride_id")
    private Ride ride;

    public Path(Long id, LocalDateTime startTime, LocalDateTime endTime, Location startingAddress,
                Location destinationAddress, double mileage, double estimatedTime, double pricePerMileage) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startingAddress = startingAddress;
        this.destinationAddress = destinationAddress;
        this.mileage = mileage;
        this.estimatedTime = estimatedTime;
        this.pricePerMileage = pricePerMileage;
    }

    public Long getId(){
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Location getStartingAddress() {
        return startingAddress;
    }

    public void setStartingAddress(Location startingAddress) {
        this.startingAddress = startingAddress;
    }

    public Location getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(Location destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public double getPricePerMileage() {
        return pricePerMileage;
    }

    public void setPricePerMileage(double pricePerMileage) {
        this.pricePerMileage = pricePerMileage;
    }
}
