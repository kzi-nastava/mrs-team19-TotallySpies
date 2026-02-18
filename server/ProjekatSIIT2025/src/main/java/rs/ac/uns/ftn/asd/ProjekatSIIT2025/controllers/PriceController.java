package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.UpdatePriceDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehiclePricing;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.PricingService;

@RestController
@RequestMapping("/api/v1/prices")
public class PriceController {

    @Autowired
    private PricingService pricingService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<?> updatePrice(@Valid @RequestBody UpdatePriceDTO dto) {
        pricingService.updateBasePrice(dto.getVehicleType(), dto.getNewPrice());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    public ResponseEntity<List<VehiclePricing>> getCurrentPrices() {
        return ResponseEntity.ok(pricingService.getAllPrices());
    }
}