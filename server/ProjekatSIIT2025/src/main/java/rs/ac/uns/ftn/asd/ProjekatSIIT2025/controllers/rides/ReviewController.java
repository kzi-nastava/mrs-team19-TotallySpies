package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.ReviewRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.ReviewResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Review;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.ReviewType;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.ReviewService;

@RestController
@RequestMapping("api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping(value = "/{rideId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewResponseDTO> createVehicleReview(
            @PathVariable Long rideId, 
            @Valid @RequestBody ReviewRequestDTO request) {
        
        Review review = reviewService.saveReview(rideId, request, ReviewType.VEHICLE);
                
        return new ResponseEntity<>(new ReviewResponseDTO(review), HttpStatus.OK);
    }

    @PostMapping(value = "/{rideId}/driver", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewResponseDTO> createDriverReview(
            @PathVariable Long rideId, 
            @Valid @RequestBody ReviewRequestDTO request) {
        
        Review review = reviewService.saveReview(rideId, request, ReviewType.DRIVER);
        
        return new ResponseEntity<>(new ReviewResponseDTO(review), HttpStatus.OK);
    }
}