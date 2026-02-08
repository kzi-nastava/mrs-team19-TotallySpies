package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class RideDetailsDTO {
    private String driverName;
    private String carModel;
    private String profilePicture;
    private String status;
    private String pickupAddress;
    private String destinationAddress;
    private String startTime;
    private String endTime;
    private double price;
    
    public RideDetailsDTO(String driverName, String carModel, String profilePicture, String status,
            String pickupAddress, String destinationAddress, String startTime, String endTime, double price) {
        this.driverName = driverName;
        this.carModel = carModel;
        this.profilePicture = profilePicture;
        this.status = status;
        this.pickupAddress = pickupAddress;
        this.destinationAddress = destinationAddress;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public RideDetailsDTO() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    

}
