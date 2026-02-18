package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverActivityResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.users.DriverProfileResponseDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.DriverActivity;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverActivityRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.DriverRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DriverActivityService {
    @Autowired
    DriverRepository driverRepository;

    @Autowired
    DriverActivityRepository driverActivityRepository;

    public DriverActivityResponseDTO getActivityMinutesLast24h(String email){
        Driver driver = driverRepository.findByEmail(email);
        if (driver == null) {
            throw new RuntimeException("Driver not found");
        }

        List<DriverActivity> activities = driverActivityRepository.findByDriver(driver);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(24);

        long totalMinutes = 0;

        for (DriverActivity a : activities) {
            LocalDateTime start = a.getStartTime();
            LocalDateTime end = a.getEndTime() != null ? a.getEndTime() : now;

            // presjek sa zadnja 24h
            LocalDateTime effectiveStart = start.isBefore(from) ? from : start;
            LocalDateTime effectiveEnd = end.isAfter(now) ? now : end;

            if (effectiveEnd.isAfter(effectiveStart)) {
                long minutes = Duration.between(effectiveStart, effectiveEnd).toMinutes();
                totalMinutes += minutes;
            }
        }

        DriverActivityResponseDTO dto = new DriverActivityResponseDTO();
        dto.setMinutesLast24h(totalMinutes);

        return dto;
    }

    public void startActivity(Driver driver){
        DriverActivity activity = new DriverActivity();
        activity.setDriver(driver);
        activity.setStartTime(LocalDateTime.now());
        activity.setEndTime(null);

        driverActivityRepository.save(activity);
    }

    public void endActivity(Driver driver){
        DriverActivity activity =
                driverActivityRepository
                        .findFirstByDriverAndEndTimeIsNullOrderByStartTimeDesc(driver)
                        .orElse(null);

        if (activity != null) {
            activity.setEndTime(LocalDateTime.now());
            activity.setDriver(driver);
            driverActivityRepository.save(activity);
        }
    }
}
