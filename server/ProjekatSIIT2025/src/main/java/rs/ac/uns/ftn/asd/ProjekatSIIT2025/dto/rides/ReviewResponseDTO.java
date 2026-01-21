package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Review;

public class ReviewResponseDTO {
    private Long id;
    private int rating;
    private String comment;
    private Long passengerId;

    public ReviewResponseDTO() {
    }
    
    public ReviewResponseDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getGrade();
        this.comment = review.getComment();
        this.passengerId = review.getPassenger().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

     
}