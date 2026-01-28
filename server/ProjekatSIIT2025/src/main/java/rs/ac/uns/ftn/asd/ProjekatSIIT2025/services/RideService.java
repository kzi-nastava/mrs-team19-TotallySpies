package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.time.Duration;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.auth.MailBody;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverActivityResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.UserProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;

import static java.awt.geom.Point2D.distance;
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

    @Autowired
    DriverActivityService driverActivityService;

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

    @Transactional
    public List<RideFinishResponseDTO> findScheduledRides() {
        // the current implementation is to display active ride also so we can finish it thorugh UI
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Long driverId = driverRepository.findByEmail(email).getId();
        List<RideStatus> statuses = List.of(RideStatus.ACTIVE, RideStatus.SCHEDULED);
        List<Ride> rides = rideRepository.findByDriverIdAndStatusIn(driverId, statuses);

        return rides.stream().map(r -> {
            RideFinishResponseDTO dto = new RideFinishResponseDTO();
            dto.setRideId(r.getId());
            dto.setStatus(r.getStatus());
            dto.setTotalPrice(r.getTotalPrice());
            dto.setFinishTime(r.getFinishedAt());
            
            // if the ride is started show started at, if not, then scheduledFor
            dto.setDisplayTime(r.getStartedAt() != null ? r.getStartedAt() : r.getScheduledFor());

            if (r.getStops() != null && !r.getStops().isEmpty()) {
                dto.setStartLocation(r.getStops().get(0).getAddress());
                dto.setEndLocation(r.getStops().get(r.getStops().size() - 1).getAddress());
            }

            dto.setPassengers(r.getPassengers().stream()
                .map(p -> new UserProfileResponseDTO(p.getName(), p.getLastName(), p.getProfilePicture()))
                .collect(Collectors.toList()));
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // == user.getEmail()
        User user = userRepository.findByEmail(email);
        if(user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        Ride ride = rideRepository.findById(dto.getRideId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found!")
                );
        if (user instanceof Passenger){
            //passenger can cancel a ride 10 minutes before the ride
            Duration untilRideStart = Duration.between(LocalDateTime.now(), ride.getStartedAt());
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
        Ride ride = rideRepository.findById(dto.getRideId())
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
                ride, dto.getTime(), dto.getReason());
        panicNotificationRepository.save(panicNotification);
        ride.setPanic(true);
        rideRepository.save(ride);
    }

    @Transactional
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
        ride.setFinishedAt(dto.getNewEndTime());
        ride.setTotalPrice(dto.getNewTotalPrice());
        ride.setStatus(RideStatus.STOPPED);
        List<RideStop> rideStops = ride.getStops();
        if(rideStops == null || rideStops.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride has no stops");
        }
        RideStop removedStop = rideStops.remove(rideStops.size() - 1);
        removedStop.setRide(null);
        RideStop newStop = dto.getNewDestination();
        newStop.setRide(ride);
        newStop.setOrderIndex(rideStops.size());
        rideStops.add(newStop);
        //rideRepository.save(ride); not needed because JPA transcational
        //hibernate will automatically detect changes and persist them when the transaction commits
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

    @Transactional
    public List<DriverRideHistoryResponseDTO> getDriverHistory(Long id, String from, String to) {
        //  if sent, transform String dates into LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fromDate;
        LocalDateTime toDate;
        try {
        if (from != null) {
            fromDate = LocalDate.parse(from, formatter).atStartOfDay();
        } else {
            fromDate = LocalDateTime.of(2020,1 ,1 ,0, 0, 0);
        }
        if (to != null) {
            toDate = LocalDate.parse(to, formatter).atTime(23, 59, 59);
        } else {
            toDate =  LocalDateTime.now();
        }
        } catch (DateTimeException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "format of date must be yyyy-MM-dd");
        }

        List<Ride> rides = rideRepository.findAllByDriverIdAndStartedAtBetween(id, fromDate, toDate);

        return rides.stream().map(ride -> {
            DriverRideHistoryResponseDTO dto = new DriverRideHistoryResponseDTO();
            dto.setId(ride.getId());
            dto.setStartTime(ride.getStartedAt());
            dto.setEndTime(ride.getFinishedAt());
            dto.setPrice(ride.getTotalPrice());
            dto.setPassengers(ride.getPassengers().stream().map(p -> p.getName() + " " + p.getLastName()).collect(Collectors.toList()));
            if (!ride.getStops().isEmpty()) {
                dto.setStartLocation(ride.getStops().stream()
                    .min(Comparator.comparingInt(RideStop::getOrderIndex)).get().getAddress());
                dto.setEndLocation(ride.getStops().stream()
                    .max(Comparator.comparingInt(RideStop::getOrderIndex)).get().getAddress());
            }

            if (ride.getRideCancellation() != null) {
                dto.setCancelled(true);
                dto.setCancelledBy(ride.getRideCancellation().getUser().getEmail());
                dto.setCancellReason(ride.getRideCancellation().getCancellationReason());
            } else {
                dto.setCancelled(false);
            }

            if (ride.getPanicNotification() != null) {
                dto.setPanicPressed(true);
                dto.setPanicReason(ride.getPanicNotification().getReason());
            } else {
                dto.setPanicPressed(false);
            }

            return dto;

        }).collect(Collectors.toList());
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
        List<RideStop> stops = new ArrayList<>();
        for (int i = 0; i < dto.getLocations().size(); i++) {
            RideStopDTO loc = dto.getLocations().get(i);

            RideStop rs = new RideStop();
            rs.setAddress(loc.getAddress());
            rs.setOrderIndex(i);
            rs.setLatitude(loc.getLat());
            rs.setLongitude(loc.getLng());
            rs.setRide(ride);

            stops.add(rs);
        }

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

    public void startRide(Long rideId, String email) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found!")
                );

        Driver driver = driverRepository.findByEmail(email);
        if (driver == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found!");
        }

        if (!ride.getDriver().getId().equals(driver.getId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This driver is not assigned to this ride!");
        }

        if (ride.getStatus() != RideStatus.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride cannot be started. Status: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.ACTIVE);
        ride.setStartedAt(LocalDateTime.now());
        rideRepository.save(ride);
    }
}
