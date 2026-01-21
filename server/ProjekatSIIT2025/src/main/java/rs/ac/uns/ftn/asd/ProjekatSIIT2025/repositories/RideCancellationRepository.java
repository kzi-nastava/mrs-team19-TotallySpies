package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideCancellation;

@Repository
public interface RideCancellationRepository extends JpaRepository<RideCancellation, Long> {
}
