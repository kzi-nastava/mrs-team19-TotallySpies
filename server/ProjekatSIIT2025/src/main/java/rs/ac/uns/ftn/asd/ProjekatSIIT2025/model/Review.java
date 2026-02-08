package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="passenger_id")
    private Passenger passenger;

    private String comment;

    private int grade;

    @Enumerated(EnumType.STRING)
    private ReviewType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ride_id")
    private Ride ride;

    public Review() {
    }

    public Review(Long id, Passenger passenger, String comment, int grade, ReviewType type, Ride ride) {
        this.id = id;
        this.passenger = passenger;
        this.comment = comment;
        this.grade = grade;
        this.type = type;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public ReviewType getType() {
        return type;
    }

    public void setType(ReviewType type) {
        this.type = type;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }
}
