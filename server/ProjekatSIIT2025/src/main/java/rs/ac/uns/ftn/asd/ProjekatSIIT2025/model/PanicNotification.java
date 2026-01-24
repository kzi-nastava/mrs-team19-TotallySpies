package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PanicNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    User user;
    @OneToOne
    Ride ride;
    LocalDateTime time;
    String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public PanicNotification(User user, Ride ride, LocalDateTime time, String reason) {
        this.user = user;
        this.ride = ride;
        this.time = time;
        this.reason = reason;
    }
}
