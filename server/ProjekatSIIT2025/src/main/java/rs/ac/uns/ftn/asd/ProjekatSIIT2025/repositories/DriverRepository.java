package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByEmail(String email);
    Driver findByEmail(String email);
    List<Driver> findByIsActiveTrue();
}
