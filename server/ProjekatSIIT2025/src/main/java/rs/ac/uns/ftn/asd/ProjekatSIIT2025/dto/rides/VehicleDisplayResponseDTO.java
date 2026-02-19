package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class VehicleDisplayResponseDTO {

    private Long id;
    private Long driverId;
    private double currentLat;
    private double currentLng;
    private boolean isBusy;
    private String driverName;

    public VehicleDisplayResponseDTO() {
    }

    public VehicleDisplayResponseDTO(Long id, Long driverId, double currentLat, double currentLng, boolean isBusy, String driverName) {
        this.id = id;
        this.driverId = driverId;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.isBusy = isBusy;
        this.driverName = driverName;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    
}