package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findFirstByDriverIdAndStatusOrderByStartedAtAsc(Long driverId, RideStatus scheduled);
    List<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);
    Optional<Ride> findFirstByDriverIdAndStatusAndStartedAtAfterOrderByStartedAtAsc(Long driverId, RideStatus scheduled,
            LocalDateTime startTime);
    List<Ride> findAllByDriverIdAndStartedAtBetween(Long id, LocalDateTime fromDate, LocalDateTime toDate);
}
