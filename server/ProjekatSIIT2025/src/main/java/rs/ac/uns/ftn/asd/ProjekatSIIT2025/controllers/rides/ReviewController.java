package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.ReviewRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.ReviewResponseDTO;

@RestController
@RequestMapping("api/v1/reviews")
public class ReviewController {

    @PostMapping(value = "/{rideId}/vehicle", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewResponseDTO> createVehicleReview(
            @PathVariable Long rideId, 
            @RequestBody ReviewRequestDTO request) {
        
        ReviewResponseDTO response = new ReviewResponseDTO();
        response.setId(101L);
        response.setRating(request.getRating());
        response.setComment(request.getComment());
        response.setPassengerEmail("putnik@gmail.com");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/{rideId}/driver", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewResponseDTO> createDriverReview(
            @PathVariable Long rideId, 
            @RequestBody ReviewRequestDTO request) {
        
        ReviewResponseDTO response = new ReviewResponseDTO();
        response.setId(102L);
        response.setRating(request.getRating());
        response.setComment(request.getComment());
        response.setPassengerEmail("putnik@gmail.com");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}