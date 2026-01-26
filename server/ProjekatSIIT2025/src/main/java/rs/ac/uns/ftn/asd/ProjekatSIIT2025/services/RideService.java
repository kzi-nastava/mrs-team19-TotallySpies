package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
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
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.StopRideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.RideTrackingDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.CancelRideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.InconsistencyReportRequestDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.PanicNotificationDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.PanicNotificationRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.ReportRepository;
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
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private ReportRepository reportRepository;

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
        Optional<Ride> nextRide = rideRepository.findFirstByDriverIdAndStatusOrderByStartedAtAsc(ride.getDriver().getId(), RideStatus.SCHEDULED);
        Long nextRideId = null;
        if (nextRide.isPresent()) {
            nextRideId = nextRide.get().getId();
        }

        //map into dto
        RideFinishResponseDTO response = new RideFinishResponseDTO();
        response.setRideId(ride.getId());
        response.setFinishTime(ride.getFinishedAt());
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
        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found!")
                );
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
                );
        if (user instanceof Passenger){
            //passenger can cancel a ride 10 minutes before the ride
            Duration untilRideStart = Duration.between(LocalDateTime.now(), ride.getStartTime());
            if(untilRideStart.isNegative()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride already started");
            }
            if (untilRideStart.toMinutes() < 10) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger can cancel only at least 10 minutes before the ride");
            }
        }
        else{
            if (!ride.getDriver().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Driver not assigned to this ride");
            }
        }
        if (dto.getRejectionReason() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride cancellation reason can not be null!");
        if(dto.getTime() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Time of ride rejection reason can not be null!");
        if(ride.getStatus() == RideStatus.ACTIVE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride has already started!");
        }
        else{
            ride.setStatus(RideStatus.CANCELLED);
            RideCancellation rideCancellation = new RideCancellation(user, ride,dto.getRejectionReason(), dto.getTime());
            rideCancellationRepository.save(rideCancellation);
            ride.setRideCancellation(rideCancellation);
            rideRepository.save(ride);
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
            dto.setTime(LocalDateTime.now());
        }
        if(dto.getReason() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Panic reason can not be null!");
        }
        PanicNotification panicNotification = new PanicNotification(user,
                rideElena, dto.getTime(), dto.getReason());
        panicNotificationRepository.save(panicNotification);
        ride.setPanic(true);
        rideRepository.save(ride);
    }

    public void stopRide(StopRideDTO dto){
        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found!")
                );
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!")
                );
        if(dto.getNewEndTime() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time can not be null!");
        }
        ride.setEndTime(dto.getNewEndTime());
        ride.setTotalPrice(dto.getNewTotalPrice());
        ride.setStatus(RideStatus.STOPPED);
        List<Path> paths = ride.getPaths();
        if(paths == null || paths.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride has no paths");
        }
        paths.get(paths.size() -  1).setDestinationAddress(dto.getNewDestinationAddress());
        ride.setPaths(paths);
        rideRepository.save(ride);
    }

    public int getETA(double currentLat, double currentLng, double destLat, double destLng) {
        // calculate distance from the destination point and current position
        double distance = calculateHaversine(currentLat, currentLng, destLat, destLng);
        
        // we supposed the average speed in town is 35km/h
        double time = (distance / 35.0) * 60.0;
        
        return (int) Math.round(time);
    }

    private double calculateHaversine(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c; // Distance in km
        return distance;
    }
    
    @Transactional
    public RideTrackingDTO getRideTrackingInfo(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null || ride.getDriver() == null) return null;

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ride is not active.");
        }

        Vehicle vehicle = ride.getDriver().getVehicle();

        RideStop destination = ride.getStops().stream()
            .max(Comparator.comparingInt(RideStop::getOrderIndex))
            .orElse(ride.getStops().get(ride.getStops().size() - 1));

        int eta = getETA(
            vehicle.getCurrentLat(), 
            vehicle.getCurrentLng(), 
            destination.getLatitude(), 
            destination.getLongitude()
        );

        return new RideTrackingDTO(
            vehicle.getId(),
            vehicle.getCurrentLat(), 
            vehicle.getCurrentLng(), 
            eta, 
            ride.getStatus().toString());
    }

    @Transactional
    public boolean reportInconsistency(Long id, InconsistencyReportRequestDTO request) {

        Ride ride = rideRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ride is not found"));
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Passenger passenger = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "passenger not found"));

        boolean participated = ride.getPassengers().stream().anyMatch(p -> p.getId().equals(passenger.getId()));
        if (!participated) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "you cannot report a ride you did not participate in!");
        }
        if (ride.getStatus() != RideStatus.ACTIVE || passenger == null) {
            System.out.println("ID putnika iz DTO-a: " + passenger);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ride is not active.");
        }

        Report report = new Report();
        report.setPassenger(passenger);
        report.setReportReason(request.getDescription());
        report.setRide(ride);
        reportRepository.save(report);

        // we need to save it in rides also
        ride.getReports().add(report);
        rideRepository.save(ride);

        return true;
    }
}
