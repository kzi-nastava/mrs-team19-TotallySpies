package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

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
    private Ride ride;

    public Report() {
    }

    public Report(Long id, Passenger passenger, String reportReason) {
        this.id = id;
        this.passenger = passenger;
        this.reportReason = reportReason;
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

    public void setPassengerId(Passenger passenger) {
        this.passenger = passenger;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }
}
