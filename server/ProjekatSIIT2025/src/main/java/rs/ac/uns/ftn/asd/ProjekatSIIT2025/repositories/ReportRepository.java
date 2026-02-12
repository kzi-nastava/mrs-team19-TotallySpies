package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Report;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>{
    List<Report> findByRide_Id(Long rideId);
}
