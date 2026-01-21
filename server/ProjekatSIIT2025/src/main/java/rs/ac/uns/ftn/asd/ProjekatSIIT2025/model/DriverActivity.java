package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DriverActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Driver driver;

    @Column(nullable = false)
    private LocalDateTime startTime;
    private LocalDateTime endTime; // null dok je aktivan

    public DriverActivity() {}

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
}
