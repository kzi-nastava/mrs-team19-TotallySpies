package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="passenger_id")
    private Passenger passenger;

    private String reportReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ride_id")
    @JsonIgnore
    private Ride ride;

    public Report() {
    }

    
    public Report(Long id, Passenger passenger, String reportReason, Ride ride) {
        this.id = id;
        this.passenger = passenger;
        this.reportReason = reportReason;
        this.ride = ride;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Passenger getPassenger() {
        return passenger;
    }


    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }


    public String getReportReason() {
        return reportReason;
    }


    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }


    public Ride getRide() {
        return ride;
    }


    public void setRide(Ride ride) {
        this.ride = ride;
    }

}
