package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import jakarta.transaction.Transactional;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.NotificationType;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationSchedulerService {
    @Autowired
    RideRepository rideRepository;

    @Autowired
    NotificationService notificationService;

    @Transactional
    @Scheduled(fixedRate = 60 * 1000) // svaki minut
    public void processUsersReminderNotification() {
        LocalDateTime now = LocalDateTime.now();

        // uzmi sve voznje koje su SCHEDULED i jos nisu pocele
        List<Ride> upcomingRides = rideRepository.findByStatus(RideStatus.SCHEDULED);

        for (Ride ride : upcomingRides) {
            if (ride.getScheduledFor() == null) continue;

            long minutesToStart = Duration.between(now, ride.getScheduledFor()).toMinutes();

            if (minutesToStart <= 0) continue; // voznja je vec pocela ili u toku

            // inicijalizacija liste ako je null
            if (ride.getSentNotificationMinutes() == null) {
                ride.setSentNotificationMinutes(new ArrayList<>());
            }

            // salji reminder na 15 minuta prije i na svakih 5 minuta do starta
            List<Integer> reminderTimes = new ArrayList<>();
            reminderTimes.add(15);
            reminderTimes.add(10);
            reminderTimes.add(5);

            for (Integer reminderMinute : reminderTimes) {
                if (minutesToStart <= reminderMinute && !ride.getSentNotificationMinutes().contains(reminderMinute)) {
                    notificationService.notifyUser(
                            ride.getCreator(),
                            ride,
                            "Reminder: Your ride starts in " + minutesToStart + " minutes",
                            NotificationType.RIDE_REMINDER
                    );

                    // dodaj u listu da ne saljemo ponovo notifikaciju za isto vrijeme
                    ride.getSentNotificationMinutes().add(reminderMinute);
                }
            }

            rideRepository.save(ride);
        }
    }
}
