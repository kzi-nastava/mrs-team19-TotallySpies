package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    Optional<Passenger> findByEmail(String email);
    
}
