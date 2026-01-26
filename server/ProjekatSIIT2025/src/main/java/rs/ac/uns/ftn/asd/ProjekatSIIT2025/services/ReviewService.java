package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.ReviewRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Review;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ReviewType;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ReviewRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    RideRepository rideRepository;
    @Autowired
    PassengerRepository passengerRepository;

    @Transactional
    public Review saveReview (long rideId, ReviewRequestDTO request, ReviewType type) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Passenger passenger = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "passenger not found"));

        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ride is not found"));
        
        validateReviewTime(ride.getFinishedAt());

        boolean participated = ride.getPassengers().stream().anyMatch(p -> p.getId().equals(passenger.getId()));
        if (!participated) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you cannot review a ride you did not participate in!");
        }
        if (!ride.getStatus().toString().equals("COMPLETED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ride is not finished yet");
        }

        Review review = new Review();
        review.setGrade(request.getRating());
        review.setComment(request.getComment());
        review.setRide(ride);
        review.setPassenger(passenger);
        review.setType(type);

        return reviewRepository.save(review);
    }

    private void validateReviewTime(LocalDateTime endTime) {
        if (endTime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ride isnt finished yet");
        }
        LocalDateTime deadline = endTime.plusDays(3);
        if (LocalDateTime.now().isAfter(deadline)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "review deadline has expired (3 days limit)");
        }
    }
}