package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.DriverActivity;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverActivityRepository extends JpaRepository<DriverActivity, Long> {
    List<DriverActivity> findByDriver(Driver driver);
    Optional<DriverActivity> findFirstByDriverAndEndTimeIsNullOrderByStartTimeDesc(Driver driver);
}
