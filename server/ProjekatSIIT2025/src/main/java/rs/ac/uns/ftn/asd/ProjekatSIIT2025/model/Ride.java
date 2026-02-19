package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Driver driver;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Passenger> passengers;

    private boolean panic;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("orderIndex ASC")
    private List<RideStop> stops;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @Column(nullable = false)
    private double distanceKm;
    private double estimatedTime;

    private boolean babiesTransport;
    private boolean petsTransport;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private LocalDateTime createdAt;
    private LocalDateTime scheduledFor;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL)
    private RideCancellation rideCancellation;

    @OneToMany
    private List<Report> reports;

    @OneToMany
    private List<Review> reviews;

    @OneToOne(mappedBy = "ride", cascade = CascadeType.ALL)
    private PanicNotification panicNotification;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sequence ASC")
    private List<RoutePoint> routePoints;

    @ManyToOne
    private Passenger creator;

    @ElementCollection
    private List<Integer> sentNotificationMinutes;

    @Column(nullable = false)
    private Double totalPrice;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean getIsPanic() {
        return panic;
    }

    public void setPanic(boolean panic) {
        this.panic = panic;
    }

    public List<RideStop> getStops() {
        return stops;
    }

    public void setStops(List<RideStop> stops) {
        this.stops = stops;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public RideCancellation getRideCancellation() {
        return rideCancellation;
    }

    public void setRideCancellation(RideCancellation rideCancellation) {
        this.rideCancellation = rideCancellation;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public PanicNotification getPanicNotification() {
        return panicNotification;
    }

    public void setPanicNotification(PanicNotification panicNotification) {
        this.panicNotification = panicNotification;
    }

    public List<RoutePoint> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePoint> routePoints) {
        this.routePoints = routePoints;
    }

    public Passenger getCreator() {
        return creator;
    }

    public void setCreator(Passenger creator) {
        this.creator = creator;
    }

    public List<Integer> getSentNotificationMinutes() {
        return sentNotificationMinutes;
    }

    public void setSentNotificationMinutes(List<Integer> sentNotificationMinutes) {
        this.sentNotificationMinutes = sentNotificationMinutes;
    }

    public boolean isPanic() {
        return panic;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

}
