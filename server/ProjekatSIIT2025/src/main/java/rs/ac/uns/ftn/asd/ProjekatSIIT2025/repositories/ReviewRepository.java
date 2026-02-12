package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.type = 'DRIVER' AND r.ride.driver.id = :driverId")
    List<Review> findAllDriverReviews(@Param("driverId") Long driverId);
    List<Review> findByRide_Id(Long rideId);
    
}
