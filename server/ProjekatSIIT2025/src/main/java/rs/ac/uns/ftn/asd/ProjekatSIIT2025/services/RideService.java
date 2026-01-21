package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.CancelRideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideCancellation;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideCancellationRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;

@Service
public class RideService {
    @Autowired
    RideRepository rideRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RideCancellationRepository rideCancellationRepository;

    public void cancelRide(CancelRideDTO dto){
        //check if ride is not started yet
        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found!")
                );
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
                );
        if (dto.getRejectionReason() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride cancellation reason can not be null!");
        if(dto.getTime() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Time of ride rejection reason can not be null!");

        if(ride.getStatus() == RideStatus.ACTIVE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride has already started!");
        }
        else{
            ride.setStatus(RideStatus.CANCELLED);
            RideCancellation rideCancellation = new RideCancellation(user,ride,dto.getRejectionReason(), dto.getTime());
            rideCancellationRepository.save(rideCancellation);
            ride.setRideCancellation(rideCancellation);
            rideRepository.save(ride);
        }

    }
}
