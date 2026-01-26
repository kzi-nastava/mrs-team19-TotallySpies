package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Passenger extends User {
    @ManyToMany(mappedBy = "passengers")
    private List<Ride> rides = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Ride> favouriteRides = new ArrayList<>();

    public Passenger() {
    }

    public Passenger(ArrayList<Ride> rides, ArrayList<Ride> favouritePaths) {
        this.rides = rides;
        this.favouriteRides = favouritePaths;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public List<Ride> getFavouritePaths() {
        return favouriteRides;
    }

    public void setFavouritePaths(List<Ride> favouritePaths) {
        this.favouriteRides = favouritePaths;
    }
}
