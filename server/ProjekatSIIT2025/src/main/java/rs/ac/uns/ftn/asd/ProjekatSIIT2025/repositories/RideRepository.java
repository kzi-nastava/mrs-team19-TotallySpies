package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findNextRide(Long driverId, List<RideStatus> statuses);
    List<Ride> findScheduledRides(Long driverId, RideStatus status);
}
