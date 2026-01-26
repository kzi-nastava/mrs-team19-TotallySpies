package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    Optional<Passenger> findByEmail(String email);    
}
