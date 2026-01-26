package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class RideTrackingDTO {
    private Long rideId;
    private double vehicleLat;
    private double vehicleLng;
    private int eta; 
    private String status;
    
    public RideTrackingDTO(Long rideId, double vehicleLat, double vehicleLng, int eta, String status) {
        this.rideId = rideId;
        this.vehicleLat = vehicleLat;
        this.vehicleLng = vehicleLng;
        this.eta = eta;
        this.status = status;
    }

    public RideTrackingDTO() {
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public double getVehicleLat() {
        return vehicleLat;
    }

    public void setVehicleLat(double vehicleLat) {
        this.vehicleLat = vehicleLat;
    }

    public double getVehicleLng() {
        return vehicleLng;
    }

    public void setVehicleLng(double vehicleLng) {
        this.vehicleLng = vehicleLng;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
}
