package rs.ac.uns.ftn.asd.ProjekatSIIT2025.utils;

import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.RideStop;

import java.util.Comparator;

public class RideComparator implements Comparator<Ride> {
    private final String sortBy;

    public RideComparator(String sortBy){
        this.sortBy = sortBy;
    }
    @Override
    public int compare(Ride r1, Ride r2) {
        String address1 = extractAddress(r1, sortBy);
        String address2 = extractAddress(r2, sortBy);
        return address1.compareToIgnoreCase(address2);
    }
    private String extractAddress(Ride ride, String sortBy) {
        if (ride.getStops() == null || ride.getStops().isEmpty()) {
            return "";
        }
        if ("pickupAddress".equals(sortBy)) {
            return safeAddress(ride.getStops().get(0));
        } else {
            return safeAddress(ride.getStops()
                    .get(ride.getStops().size() - 1));
        }
    }

    private String safeAddress(RideStop stop) {
        if (stop == null) return "";
        String address = stop.getAddress();
        return address == null ? "" : address;
    }
}
