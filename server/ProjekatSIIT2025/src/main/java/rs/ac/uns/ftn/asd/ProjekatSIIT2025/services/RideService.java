package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.LocationDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.MailBody;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideFinishResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.CancelRideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.PanicNotificationDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.PanicNotificationRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideCancellation;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideCancellationRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.UserRepository;

@Service
public class RideService {
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PanicNotificationRepository panicNotificationRepository;

    @Autowired
    RideCancellationRepository rideCancellationRepository;

    @Transactional
    public RideFinishResponseDTO finishRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ride is not found"));

        // update ride
        ride.setStatus(RideStatus.COMPLETED);
        ride.setFinishedAt(LocalDateTime.now());
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
        /*Optional<Ride> nextRide = rideRepository.findFirstByDriverIdAndStatusOrderByStartedAtAsc(ride.getDriver().getId(), RideStatus.SCHEDULED);
        Long nextRideId = null;
        if (nextRide.isPresent()) {
            nextRideId = nextRide.get().getId();
        } else {
            ride.getDriver().setFree(true);
            driverRepository.save(ride.getDriver());
        }*/

        //map into dto
        RideFinishResponseDTO response = new RideFinishResponseDTO();
        response.setRideId(ride.getId());
        response.setFinishTime(ride.getFinishedAt());
        response.setStatus(ride.getStatus());
        response.setTotalPrice(ride.getTotalPrice());
        //response.setNextRideId(nextRideId);

        return response;
    }

    public List<RideFinishResponseDTO> findScheduledRides() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long driverId = driverRepository.findByEmail(email).getId();
        List<Ride> rideElenas = rideRepository.findByDriverIdAndStatus(driverId, RideStatus.SCHEDULED);

        return rideElenas.stream().map(r -> {
            RideFinishResponseDTO dto = new RideFinishResponseDTO();
            dto.setRideId(r.getId());
            dto.setStatus(r.getStatus());
            dto.setTotalPrice(r.getTotalPrice());
            dto.setFinishTime(r.getFinishedAt());

            // we look for ride starting after this one (r.getStartTime())
            Optional<Ride> next = rideRepository.findFirstByDriverIdAndStatusAndStartedAtAfterOrderByStartedAtAsc(
                driverId, 
                RideStatus.SCHEDULED, 
                r.getStartedAt()
            );
            
            next.ifPresent(nextRide -> dto.setNextRideId(nextRide.getId()));

            return dto;
        }).collect(Collectors.toList());
        // needs further logic for finding next (time closest) scheduled ride
    }

    
    public void cancelRide(CancelRideDTO dto){
        //check if ride is not started yet
        Ride rideElena = rideRepository.findById(dto.getRideId())
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

        if(rideElena.getStatus() == RideStatus.ACTIVE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride has already started!");
        }
        else{
            rideElena.setStatus(RideStatus.CANCELLED);
            RideCancellation rideCancellation = new RideCancellation(user, rideElena,dto.getRejectionReason(), dto.getTime());
            rideCancellationRepository.save(rideCancellation);
            rideElena.setRideCancellation(rideCancellation);
            rideRepository.save(rideElena);
        }

    }

    public void handlePanicNotification(PanicNotificationDTO dto){
        //administrators get notification
        Ride rideElena = rideRepository.findById(dto.getRideId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found!")
                );
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
                );
        if(dto.getTime() == null){
            LocalDateTime currentTime = LocalDateTime.now();
        }
        if(dto.getReason() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Panic reason can not be null!");
        }
        PanicNotification panicNotification = new PanicNotification(user,
                rideElena, dto.getTime(), dto.getReason());
        panicNotificationRepository.save(panicNotification);
    }

}
