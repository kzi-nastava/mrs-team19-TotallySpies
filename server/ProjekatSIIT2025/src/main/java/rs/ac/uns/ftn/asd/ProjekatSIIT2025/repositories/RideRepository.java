package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Ride> findByDriverIdAndStatusInAndCreatedAtBetween(Long id,List<RideStatus> statuses,Sort sort,LocalDateTime from, LocalDateTime to);
    List<Ride> findByDriverIdAndStatusInAndCreatedAtBefore(Long id,List<RideStatus> statuses,Sort sort,LocalDateTime to);
    List<Ride> findByDriverIdAndStatusInAndCreatedAtAfter(Long id,List<RideStatus> statuses,Sort sort,LocalDateTime from);
    List<Ride> findByDriverIdAndStatusIn(Long id,List<RideStatus> statuses, Sort sort);
    @Query("SELECT r FROM Ride r WHERE " +
           "(:name IS NULL OR :name = '' OR " +
           " LOWER(r.driver.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           " LOWER(r.driver.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND r.status = :status")
    List<Ride> findActiveByDriverName(@Param("name") String name, @Param("status") RideStatus status);
    List<Ride> findByStatusAndScheduledForBetween(RideStatus status, LocalDateTime from, LocalDateTime to);

    List<Ride> findByStatus(RideStatus rideStatus);

    // lista validnih voznji koje ce se racunati u statistici
    List<RideStatus> VALID_REPORT_STATUSES = List.of(RideStatus.COMPLETED, RideStatus.STOPPED);
    List<Ride> findAllByStatusInAndStartedAtBetween(List<RideStatus> statuses, LocalDateTime from, LocalDateTime to);
    List<Ride> findAllByDriverEmailAndStatusInAndStartedAtBetween(String email, List<RideStatus> statuses, LocalDateTime from, LocalDateTime to);
    // Za Putnika (Kreatora)
    @Query("SELECT r FROM Ride r WHERE r.creator.email = :email " +
            "AND r.status IN :statuses " +
            "AND r.startedAt BETWEEN :from AND :to")
    List<Ride> findAllByCreatorEmailAndStatusInAndStartedAtBetween(
            @Param("email") String email,
            @Param("statuses") List<RideStatus> statuses,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    List<Ride> findAllByCreatorEmailAndStatusIn(String email, List<RideStatus> stuteses);
    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.email = :email AND r.status = rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus.ACTIVE")
     Ride findActiveRideByPassengerEmail(@Param("email") String email);
}
