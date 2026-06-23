package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/ride-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RideRepositoryUnitTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("2.4.1 - putnik ima aktivnu voznju -> vraca true")
    public void existsByPassengersContainingAndStatus_passengerHasActiveRide_returnsTrue() {
        Passenger p1 = entityManager.find(Passenger.class, 1L);

        boolean result = rideRepository.existsByPassengersContainingAndStatus(p1, RideStatus.ACTIVE);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("2.4.1 - putnik nema aktivnu voznju -> vraca false")
    public void existsByPassengersContainingAndStatus_passengerHasNoActiveRide_returnsFalse() {
        // p2 ima samo PENDING i SCHEDULED, ne ACTIVE
        Passenger p2 = entityManager.find(Passenger.class, 2L);

        boolean result = rideRepository.existsByPassengersContainingAndStatus(p2, RideStatus.ACTIVE);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("2.4.1 - vozac bez ACTIVE/SCHEDULED voznji -> prazna lista")
    public void findByDriverIdAndStatusIn_noMatchingRides_returnsEmptyList() {
        Long driverId = 999L; // vozac koji ne postoji

        List<Ride> result = rideRepository.findByDriverIdAndStatusIn(
                driverId, List.of(RideStatus.ACTIVE, RideStatus.SCHEDULED));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("2.4.1 - vozac ima ACTIVE i SCHEDULED voznje -> vraca sve tri")
    public void findByDriverAndStatusIn_driverHasActiveAndScheduled_returnsAll() {
        Driver driver = entityManager.find(Driver.class, 100L);

        List<Ride> result = rideRepository.findByDriverAndStatusIn(
                driver, List.of(RideStatus.ACTIVE, RideStatus.SCHEDULED));

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Ride::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    @DisplayName("2.4.1 - trazimo samo COMPLETED voznje vozaca -> vraca jednu")
    public void findByDriverAndStatusIn_completedOnly_returnsOne() {
        Driver driver = entityManager.find(Driver.class, 100L);

        List<Ride> result = rideRepository.findByDriverAndStatusIn(
                driver, List.of(RideStatus.COMPLETED));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(4L);
    }

    @Test
    @DisplayName("2.4.1 - trazimo status koji vozac nema -> prazna lista")
    public void findByDriverAndStatusIn_noMatchingStatus_returnsEmpty() {
        Driver driver = entityManager.find(Driver.class, 10L);

        List<Ride> result = rideRepository.findByDriverAndStatusIn(
                driver, List.of(RideStatus.CANCELLED));

        assertThat(result).isEmpty();
    }
}
