package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class RideTrackingDTO {
    private Long rideId;
    private double vehicleLat;
    private double vehicleLng;
    private int eta; 
    private String status;
    private String driverName;
    private String carModel;
    private String profilePicture;
    private String pickupAddress;
    private String destinationAddress;

    public RideTrackingDTO(Long rideId, double vehicleLat, double vehicleLng, int eta, String status, String driverName,
            String carModel, String profilePicture, String pickupAddress, String destinationAddress) {
        this.rideId = rideId;
        this.vehicleLat = vehicleLat;
        this.vehicleLng = vehicleLng;
        this.eta = eta;
        this.status = status;
        this.driverName = driverName;
        this.carModel = carModel;
        this.profilePicture = profilePicture;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
    }

    

    public String getPickupAddress() {
        return pickupAddress;
    }


    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }



    public String getDestinationAddress() {
        return destinationAddress;
    }



    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
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

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    
}
