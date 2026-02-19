package com.ftn.mobile.data.remote.dto;

public class ReviewRequestDTO {
    private int rating;
    private String comment;

    public ReviewRequestDTO(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}