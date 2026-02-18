package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Driver;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByEmail(String email);
    Driver findByEmail(String email);
    List<Driver> findByIsActiveTrue();

    @Query("select d from Driver d where d.isActive = true")
    List<Driver> findAllActive();

    @Query("SELECT d FROM Driver d " +
            "WHERE d.isActive = true " +
            "AND d.isBlocked = false " +
            "AND d.vehicle.vehicleType = :type " +
            "AND d.vehicle.passengerCapacity >= :seats " +
            "AND (:petFriendly = false OR d.vehicle.petTransport = true) " +
            "AND (:babyFriendly = false OR d.vehicle.babyTransport = true)")
    List<Driver> findPotentialDrivers(
            @Param("type") VehicleType type,
            @Param("seats") int seats,
            @Param("petFriendly") boolean petFriendly,
            @Param("babyFriendly") boolean babyFriendly
    );
}
