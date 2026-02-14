package rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehiclePricing;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

public interface VehiclePricingRepository extends JpaRepository<VehiclePricing, Long> {

    VehiclePricing findByVehicleType(VehicleType type);
}