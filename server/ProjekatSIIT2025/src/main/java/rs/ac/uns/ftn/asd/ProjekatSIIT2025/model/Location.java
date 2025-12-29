package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

public class Location {
    private Long id;
    private double longitude;
    private double latitude;

    public Location(Long id, double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
