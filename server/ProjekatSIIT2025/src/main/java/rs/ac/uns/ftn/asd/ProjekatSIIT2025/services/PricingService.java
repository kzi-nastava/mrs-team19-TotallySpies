package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import java.rmi.server.ExportException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehiclePricing;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.VehiclePricingRepository;

@Service
public class PricingService {

    @Autowired
    private VehiclePricingRepository pricingRepository;
    
    private static final double PRICE_PER_KM = 120.0;

    public void updateBasePrice(VehicleType type, Double newPrice) {
        try {
            VehiclePricing pricing = pricingRepository.findByVehicleType(type);            
            pricing.setBasePrice(newPrice);
            pricingRepository.save(pricing);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }

    }

    public Double calculatePrice(VehicleType type, double distanceInKm) {
        try {
            VehiclePricing pricing = pricingRepository.findByVehicleType(type);        
            return pricing.getBasePrice() + (distanceInKm * PRICE_PER_KM);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return 0.0;
        }

    }

    public List<VehiclePricing> getAllPrices() {
        return pricingRepository.findAll();
    }
}