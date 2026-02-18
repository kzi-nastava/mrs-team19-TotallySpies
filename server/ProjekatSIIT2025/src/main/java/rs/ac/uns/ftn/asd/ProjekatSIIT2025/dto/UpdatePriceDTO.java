package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

public class UpdatePriceDTO {
    @NotNull(message = "vehicle type is mandatory")
    private VehicleType vehicleType;
    @NotNull(message = "you must put price")
    @Positive(message = "price must be positive number")
    private Double newPrice;

    public UpdatePriceDTO() {}

    public UpdatePriceDTO(VehicleType vehicleType, Double newPrice) {
        this.vehicleType = vehicleType;
        this.newPrice = newPrice;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Double newPrice) {
        this.newPrice = newPrice;
    }
}