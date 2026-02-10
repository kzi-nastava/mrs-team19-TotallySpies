package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.NotificationDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.NotificationRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RideSchedulerService {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RideService rideService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 3 * 60 * 1000) // svake 3 minute
    public void processScheduledRides() {

        LocalDateTime now = LocalDateTime.now();

        // sve PENDING voznje koje pocinju u narednih 30 min
        List<Ride> rides = rideRepository
                .findByStatusAndScheduledForBetween(
                        RideStatus.PENDING,
                        now,
                        now.plusMinutes(30)
                );

        for (Ride ride : rides) {

            long minutesToStart = Duration
                    .between(now, ride.getScheduledFor())
                    .toMinutes();

            System.out.println("Checking for ride " + ride.getId());

            // ako je manje od 15 min, a nema vozaca - odbij
            if (minutesToStart <= 15) {
                ride.setStatus(RideStatus.REJECTED);
                rideRepository.save(ride);
                notifyCreator(ride, "No available drivers, your ride has been rejected", NotificationType.RIDE_REJECTED);
                continue;
            }

            Driver d = rideService.findBestDriver(rideService.fromRide(ride));

            if (d != null) {
                ride.setDriver(d);
                ride.setStatus(RideStatus.SCHEDULED);
                notifyCreator(ride, "Driver assigned: " + d.getName(), NotificationType.NEW_RIDE);
                notifyDriver(ride, "You have a new ride assigned", NotificationType.NEW_RIDE);
            }
        }

        rideRepository.saveAll(rides);
    }

    private void notifyCreator(Ride ride, String message, NotificationType type) {
        notificationService.notifyUser(ride.getCreator(), ride, message, type);
    }

    private void notifyDriver(Ride ride, String message, NotificationType type) {
        if (ride.getDriver() != null) {
            notificationService.notifyUser(ride.getDriver(), ride, message, type);
        }
    }
}
