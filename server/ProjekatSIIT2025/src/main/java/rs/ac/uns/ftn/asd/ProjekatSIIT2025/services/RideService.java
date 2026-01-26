package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.MailBody;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverActivityResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideCancellation;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.User;

import static java.awt.geom.Point2D.distance;

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
    DriverActivityService driverActivityService;

    @Autowired
    PassengerRepository passengerRepository;

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

    @Transactional
    public CreateRideResponseDTO createRide(CreateRideRequestDTO dto, String creatorEmail) {

        // get ride creator
        Passenger creator = passengerRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found"));

        boolean creatorHasActiveRide = rideRepository.existsByPassengersContainingAndStatus(creator, RideStatus.ACTIVE);
        if (creatorHasActiveRide) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already have an active ride");
        }

        // other passengers
        List<Passenger> passengers = new ArrayList<>();
        passengers.add(creator);

        if (dto.getPassengerEmails() != null) {
            for (String email : dto.getPassengerEmails()) {
                Passenger p = passengerRepository.findByEmail(email)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Passenger not found: " + email));

                // check for every passenger if they have an active ride
                boolean hasActiveRide = rideRepository.existsByPassengersContainingAndStatus(p, RideStatus.ACTIVE);
                if (hasActiveRide) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Passenger " + p.getName() + " " + p.getLastName() + " already has an active ride");
                }

                passengers.add(p);
            }
        }

        Driver driver = findBestDriver(dto);
        if (driver == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No available drivers");
        }

        double basePrice = priceByVehicleType(dto.getVehicleType());
        double totalPrice = basePrice + dto.getDistanceKm() * 120;

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setPassengers(passengers);
        ride.setStatus(RideStatus.SCHEDULED);

        ride.setVehicleType(dto.getVehicleType());
        ride.setBabiesTransport(dto.isBabyTransport());
        ride.setPetsTransport(dto.isPetTransport());
        ride.setTotalPrice(totalPrice);
        ride.setDistanceKm(dto.getDistanceKm());
        ride.setEstimatedTime(dto.getEstimatedTime());

        ride.setCreatedAt(LocalDateTime.now());

        // Stops
        List<RideStop> stops = dto.getLocations().stream()
                .map(loc -> {
                    RideStop rs = new RideStop();
                    rs.setAddress(loc.getAddress());
                    rs.setLatitude(loc.getLat());
                    rs.setLongitude(loc.getLng());
                    rs.setRide(ride);
                    return rs;
                }).toList();

        ride.setStops(stops);

        rideRepository.save(ride);

        CreateRideResponseDTO response = new CreateRideResponseDTO();
        response.setRideId(ride.getId());
        response.setStatus(ride.getStatus());
        response.setDriverEmail(driver.getEmail());
        response.setDriverName(driver.getName() + " " + driver.getLastName());
        response.setDistanceKm(ride.getDistanceKm());
        response.setEstimatedTime(ride.getEstimatedTime());
        response.setMessage("Ride successfully created and driver assigned.");

        return response;
    }

    private Driver findBestDriver(CreateRideRequestDTO dto) {

        int requiredSeats = 1 +
                (dto.getPassengerEmails() == null ? 0 : dto.getPassengerEmails().size());

        // get driver whose vehicles are compatible with ride request
        List<Driver> potentialDrivers = driverRepository.findPotentialDrivers(
                dto.getVehicleType(),
                requiredSeats,
                dto.isPetTransport(),
                dto.isBabyTransport()
        );
        if (potentialDrivers.isEmpty()) return null;

        RideStopDTO pickup = dto.getLocations().get(0);

        // list for free drivers
        List<Driver> freeDrivers = new ArrayList<>();
        // map for busy drivers and the location where their current active ride finishes
        Map<Driver, RideStop> eligibleBusyDrivers = new HashMap<>();

        for (Driver d : potentialDrivers) {
            // check driver status
            // a driver can have at most one ACTIVE and one SCHEDULED ride to be eligible
            List<Ride> driverRides = rideRepository.findByDriverAndStatusIn(
                    d, List.of(RideStatus.ACTIVE, RideStatus.SCHEDULED));

            boolean hasScheduled = driverRides.stream().anyMatch(r -> r.getStatus() == RideStatus.SCHEDULED);

            // if the driver already has a scheduled next ride, they are excluded
            if (hasScheduled) continue;

            Optional<Ride> activeRideOpt = driverRides.stream()
                    .filter(r -> r.getStatus() == RideStatus.ACTIVE)
                    .findFirst();

            if (activeRideOpt.isEmpty()) {
                // free driver: Start from current vehicle location, 0 minutes remaining
                if (canDriverTakeRide(d, dto, 0, d.getVehicle().getCurrentLat(), d.getVehicle().getCurrentLng())) {
                    freeDrivers.add(d);
                }
            } else {
                // busy driver: candidate for "ride chaining"
                Ride activeRide = activeRideOpt.get();
                long timeRemaining = getRemainingRideTime(activeRide);

                // driver is eligible if their current ride ends within 10 minutes
                if (timeRemaining <= 10) {
                    // get the final stop of the current active ride
                    RideStop lastStop = activeRide.getStops().get(activeRide.getStops().size() - 1);

                    // check work limit starting from the last stop of the current ride
                    if (canDriverTakeRide(d, dto, timeRemaining, lastStop.getLatitude(), lastStop.getLongitude())) {
                        eligibleBusyDrivers.put(d, lastStop);
                    }
                }
            }
        }

        // selection - free drivers have priority

        // if there are free drivers, select the one closest to the pickup point
        if (!freeDrivers.isEmpty()) {
            return freeDrivers.stream()
                    .min(Comparator.comparingDouble(d ->
                            distance(d.getVehicle().getCurrentLat(), d.getVehicle().getCurrentLng(),
                                    pickup.getLat(), pickup.getLng())))
                    .orElse(null);
        }

        // B) if no free drivers, select the busy one whose current finish point is closest to the new pickup
        if (!eligibleBusyDrivers.isEmpty()) {
            return eligibleBusyDrivers.entrySet().stream()
                    .min(Comparator.comparingDouble(entry -> {
                        RideStop finishPoint = entry.getValue(); // finish stop of current active ride
                        return distance(
                                finishPoint.getLatitude(), finishPoint.getLongitude(),
                                pickup.getLat(), pickup.getLng()
                        );
                    }))
                    .map(Map.Entry::getKey)
                    .orElse(null);
        }

        return null;
    }

    private long getRemainingRideTime(Ride activeRide) {
        if (activeRide == null || activeRide.getStartedAt() == null) return 0;

        long passed = Duration
                .between(activeRide.getStartedAt(), LocalDateTime.now())
                .toMinutes();

        return Math.max(0, Math.round(activeRide.getEstimatedTime() - passed));
    }

    private double priceByVehicleType(VehicleType type) {
        return switch (type) {
            case STANDARD -> 300;
            case VAN -> 500;
            case LUXURIOUS -> 800;
        };
    }

    private boolean canDriverTakeRide(Driver driver, CreateRideRequestDTO dto, long remainingMinutesOfCurrentRide, double startLat, double startLng) {
        // minutes worked in the last 24h
        DriverActivityResponseDTO activity = driverActivityService.getActivityMinutesLast24h(driver.getEmail());
        long minutesWorked = activity.getMinutesLast24h();

        // estimated duration of the new requested ride
        long rideMinutes = Math.round(dto.getEstimatedTime());

        // travel time from reference point to the new pickup location
        RideStopDTO pickup = dto.getLocations().get(0);
        double distanceToPickupKm = distance(startLat, startLng, pickup.getLat(), pickup.getLng());

        // travelTimeToPickup calculation (assuming average speed 30km/h -> 0.5km/min)
        long travelTimeToPickup = Math.round(distanceToPickupKm / 0.5);

        // total time = worked today + remaining current ride + travel to pickup + new ride duration
        long totalMinutesIfAssigned = minutesWorked + remainingMinutesOfCurrentRide + travelTimeToPickup + rideMinutes;

        // 8-hour limit = 480 minutes
        return totalMinutesIfAssigned <= 480;
    }

}
