package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private double totalPrice;

    @ManyToOne
    private Driver driver;

    @ManyToMany()
    private List<Passenger> passengers = new ArrayList<>();

    @OneToMany(mappedBy = "ride")
    private List<Path> paths;

    @OneToMany(mappedBy = "ride")
    private List<Review> reviews;

    @OneToMany(mappedBy = "ride")
    private List<Report> reports;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    private RideRejection rideRejectionId;

    private boolean panic;

    private boolean babiesTransport;

    private boolean petsTransport;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    public Ride() {
    }

    public Ride(Long id, LocalDateTime startTime, LocalDateTime endTime, double totalPrice, Driver driver,
                List<Passenger> passengers, List<Path> paths, List<Review> reviews, RideStatus status,
                RideRejection rideRejectionId, boolean panic, boolean babiesTransport, boolean petsTransport,
                VehicleType vehicleType, List<Report> reports) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.driver = driver;
        this.passengers = passengers;
        this.paths = paths;
        this.reviews = reviews;
        this.status = status;
        this.rideRejectionId = rideRejectionId;
        this.panic = panic;
        this.babiesTransport = babiesTransport;
        this.petsTransport = petsTransport;
        this.vehicleType = vehicleType;
        this.reports = reports;
    }

    public Long getId() {
        return id;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public RideRejection getRideRejectionId() {
        return rideRejectionId;
    }

    public void setRideRejectionId(RideRejection rideRejectionId) {
        this.rideRejectionId = rideRejectionId;
    }

    public boolean isPanic() {
        return panic;
    }

    public void setPanic(boolean panic) {
        this.panic = panic;
    }

    public boolean isBabiesTransport() {
        return babiesTransport;
    }

    public void setBabiesTransport(boolean babiesTransport) {
        this.babiesTransport = babiesTransport;
    }

    public boolean isPetsTransport() {
        return petsTransport;
    }

    public void setPetsTransport(boolean petsTransport) {
        this.petsTransport = petsTransport;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
}

