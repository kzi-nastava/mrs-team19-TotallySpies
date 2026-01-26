package rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStop;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.VehicleType;

import java.util.Comparator;
import java.util.List;

public class FavouriteRideDTO {
    private Long id;
    private List<RideStopDTO> locations;

    public FavouriteRideDTO(Long id, List<RideStop> stops){
        this.id = id;
        this.locations = stops.stream()
                .sorted(Comparator.comparingInt(RideStop::getOrderIndex))
                .map(s -> new RideStopDTO(
                        s.getLatitude(),
                        s.getLongitude(),
                        s.getAddress()
                ))
                .toList();
    }

    public FavouriteRideDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RideStopDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RideStopDTO> locations) {
        this.locations = locations;
    }
}