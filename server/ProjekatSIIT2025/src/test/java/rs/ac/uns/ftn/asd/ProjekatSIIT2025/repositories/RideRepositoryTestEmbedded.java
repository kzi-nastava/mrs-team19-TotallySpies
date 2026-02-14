package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStatus;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/ride-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RideRepositoryTestEmbedded {

    @Autowired
    private RideRepository rideRepository;

    @Test
    public void shouldFindScheduledRidesForDriver() {
        Long driverId = 100L;

        List<Ride> result = rideRepository.findByDriverIdAndStatusIn(
                driverId, 
                List.of(RideStatus.ACTIVE, RideStatus.SCHEDULED)
        );

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Ride::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    public void shouldFindFirstScheduledRideOrderedByStartTime() {
        Long driverId = 100L;

        Optional<Ride> result = rideRepository.findFirstByDriverIdAndStatusOrderByStartedAtAsc(
                driverId, 
                RideStatus.SCHEDULED
        );

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(2L);
        assertThat(result.get().getStatus()).isEqualTo(RideStatus.SCHEDULED);
    }
}
