package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

public class RideStopDTO {
    private String address;
    private Double lat;
    private Double lng;

    public RideStopDTO(Double latitude, Double longitude, String address) {
        this.address = address;
        this.lat = latitude;
        this.lng = longitude;
    }

    public RideStopDTO() {}

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
