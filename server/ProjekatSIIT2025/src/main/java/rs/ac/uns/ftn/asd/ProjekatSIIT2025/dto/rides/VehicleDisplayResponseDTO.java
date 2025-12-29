package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class VehicleDisplayResponseDTO {

    private Long id;
    private double currentLat;
    private double currentLng;
    private boolean isBusy;

    public VehicleDisplayResponseDTO() {
    }

    public VehicleDisplayResponseDTO(Long id, double currentLat, double currentLng, boolean isBusy) {
        this.id = id;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.isBusy = isBusy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

    
    
}