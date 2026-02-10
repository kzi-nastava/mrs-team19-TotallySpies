package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findFirstByDriverIdAndStatusOrderByStartedAtAsc(Long driverId, RideStatus scheduled);
    List<Ride> findByDriverIdAndStatusIn(Long driverId, List<RideStatus> statuses);
    Optional<Ride> findFirstByDriverIdAndStatusAndStartedAtAfterOrderByStartedAtAsc(Long driverId, RideStatus scheduled,
            LocalDateTime startTime);
    List<Ride> findAllByDriverIdAndStartedAtBetween(Long id, LocalDateTime fromDate, LocalDateTime toDate);

    boolean existsByPassengersContainingAndStatus(Passenger creator, RideStatus rideStatus);

    List<Ride> findByDriverAndStatusIn(Driver d, List<RideStatus> active);
    List<Ride> findByPassengers_IdAndStatusIn(Long id, List<RideStatus> statuses, Sort sort);
    List<Ride> findByPassengers_IdAndStatusInAndCreatedAtBetween(Long id, List<RideStatus> statuses, Sort sort, LocalDateTime from, LocalDateTime to);
    List<Ride> findByPassengers_IdAndStatusInAndCreatedAtBefore(Long id, List<RideStatus> statuses, Sort sort, LocalDateTime to);
    List<Ride> findByPassengers_IdAndStatusInAndCreatedAtAfter(Long id, List<RideStatus> statuses, Sort sort, LocalDateTime from);
    List<Ride> findByStatusAndScheduledForBetween(RideStatus status, LocalDateTime from, LocalDateTime to);

    List<Ride> findByStatus(RideStatus rideStatus);
}
