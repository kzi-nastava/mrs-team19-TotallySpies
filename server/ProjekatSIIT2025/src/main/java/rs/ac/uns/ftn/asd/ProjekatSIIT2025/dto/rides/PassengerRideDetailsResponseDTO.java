package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStop;

import java.util.List;
import java.util.Map;

public class PassengerRideDetailsResponseDTO {
    private Long rideId;
    private List<RideStop> rideStops;
    private double distanceKm;
    private double totalPrice;
    private String driverName;
    private String driverLastName;
    private String driverEmail;
    private String driverPhoneNumber;
    private Map<String, String> reportReasons; //passenger email and report reason
    private List<RideGradeDTO> rideGrades;

    public PassengerRideDetailsResponseDTO(Long rideId, List<RideStop> rideStops, double distanceKm, double totalPrice, String driverName, String driverLastName, String driverEmail, String driverPhoneNumber, Map<String, String> reportReasons, List<RideGradeDTO> rideGrades) {
        this.rideId = rideId;
        this.rideStops = rideStops;
        this.distanceKm = distanceKm;
        this.totalPrice = totalPrice;
        this.driverName = driverName;
        this.driverLastName = driverLastName;
        this.driverEmail = driverEmail;
        this.driverPhoneNumber = driverPhoneNumber;
        this.reportReasons = reportReasons;
        this.rideGrades = rideGrades;
    }

    public PassengerRideDetailsResponseDTO(Long rideId, List<RideStop> rideStops, double distanceKm, double totalPrice) {
        this.rideId = rideId;
        this.rideStops = rideStops;
        this.distanceKm = distanceKm;
        this.totalPrice = totalPrice;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public List<RideStop> getRideStops() {
        return rideStops;
    }

    public void setRideStops(List<RideStop> rideStops) {
        this.rideStops = rideStops;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public Map<String, String> getReportReasons() {
        return reportReasons;
    }

    public void setReportReasons(Map<String, String> reportReasons) {
        this.reportReasons = reportReasons;
    }

    public List<RideGradeDTO> getRideGrades() {
        return rideGrades;
    }

    public void setRideGrades(List<RideGradeDTO> rideGrades) {
        this.rideGrades = rideGrades;
    }
}
