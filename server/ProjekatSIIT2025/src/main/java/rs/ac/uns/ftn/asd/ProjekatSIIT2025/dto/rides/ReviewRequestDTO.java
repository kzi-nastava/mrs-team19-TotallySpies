package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReviewRequestDTO {
    @Min(value =  1, message = "rating must be at least 1 star")
    @Max(value = 5, message = "rating cannot be higher than 5")
    private int rating;

    @NotBlank(message = "rating cannot be blank")
    @Size(max = 500, message = "comment too long (max 500 chars)")
    private String comment;
    
    public ReviewRequestDTO() {
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
}