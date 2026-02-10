package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class RoutePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double latitude;
    private double longitude;
    private int sequence; // num of point in route (0, 1, 2, 3...)
    @ManyToOne
    private Ride ride;
    
    public RoutePoint() {
    }

    public RoutePoint(Long id, double latitude, double longitude, int sequence, Ride ride) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sequence = sequence;
        this.ride = ride;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    
}
