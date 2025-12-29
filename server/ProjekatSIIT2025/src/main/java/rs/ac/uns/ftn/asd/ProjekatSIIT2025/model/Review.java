package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

public class Review {
    private Long id;
    private Long passengerId;
    private Long rideId;
    private String comment;
    private int grade;

    public Review(Long id, Long passengerId, Long rideId, String comment, int grade) {
        this.id = id;
        this.passengerId = passengerId;
        this.rideId = rideId;
        this.comment = comment;
        this.grade = grade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
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
}
