package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.MailBody;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideFinishResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;

@Service
public class RideService {
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private EmailService emailService;

    @Transactional
    public RideFinishResponseDTO finishRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ride is not found"));

        // update ride
        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());
        rideRepository.save(ride);

        // send email to the passengers
        for (Passenger passenger : ride.getPassengers()) {
            MailBody mailBody = new MailBody(
                passenger.getEmail(), 
                "Ride Finished - SmartRide", 
                "Dear " + passenger.getName() + ", your ride has successfully finished. " +
                "Total price: " + ride.getTotalPrice() + " RSD. You can now leave a review on the app!"
            );
            try {
                emailService.sendSimpleMessage(mailBody);
            } catch (Exception e) {
                System.err.println("Failed to send email to: " + passenger.getEmail());
            }
        }

        // now we find the next ride
        Optional<Ride> nextRide = rideRepository.findFirstByDriverIdAndStatusOrderByStartTimeAsc(ride.getDriver().getId(), RideStatus.SCHEDULED);
        Long nextRideId = null;
        if (nextRide.isPresent()) {
            nextRideId = nextRide.get().getId();
        } else {
            ride.getDriver().setFree(true);
            driverRepository.save(ride.getDriver());
        }

        //map into dto
        RideFinishResponseDTO response = new RideFinishResponseDTO();
        response.setRideId(ride.getId());
        response.setFinishTime(ride.getEndTime());
        response.setStatus(ride.getStatus());
        response.setTotalPrice(ride.getTotalPrice());
        response.setNextRideId(nextRideId);

        return response;
    }

    public List<RideFinishResponseDTO> findScheduledRides() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long driverId = driverRepository.findByEmail(email).getId();
        List<Ride> rides = rideRepository.findByDriverIdAndStatus(driverId, RideStatus.SCHEDULED);

        return rides.stream().map(r -> {
            RideFinishResponseDTO dto = new RideFinishResponseDTO();
            dto.setRideId(r.getId());
            dto.setStatus(r.getStatus());
            dto.setTotalPrice(r.getTotalPrice());
            dto.setFinishTime(r.getEndTime()); 

            // we look for ride starting after this one (r.getStartTime())
            Optional<Ride> next = rideRepository.findFirstByDriverIdAndStatusAndStartTimeAfterOrderByStartTimeAsc(
                driverId, 
                RideStatus.SCHEDULED, 
                r.getStartTime()
            );
            
            next.ifPresent(nextRide -> dto.setNextRideId(nextRide.getId()));

            return dto;
        }).collect(Collectors.toList());
        // needs further logic for finding next (time closest) scheduled ride
    }
}
