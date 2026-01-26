package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("SELECT v FROM Vehicle v JOIN Driver d ON v.id = d.vehicle.id WHERE d.isActive = true")
    List<Vehicle> findAllActiveVehicles();
}
