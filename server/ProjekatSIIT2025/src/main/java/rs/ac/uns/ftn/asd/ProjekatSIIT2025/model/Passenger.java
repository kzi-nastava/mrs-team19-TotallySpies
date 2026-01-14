package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Passenger extends User {
    @ManyToMany(cascade = {CascadeType.ALL}, mappedBy = "passengers")
    private List<Ride> rides = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL})
    private List<Path> favouritePaths = new ArrayList<>();

    public Passenger() {
    }

    public Passenger(ArrayList<Ride> rides, ArrayList<Path> favouritePaths) {
        this.rides = rides;
        this.favouritePaths = favouritePaths;
    }

    public List<Ride> getRides() {
        return rides;
    }

    public void setRides(List<Ride> rides) {
        this.rides = rides;
    }

    public List<Path> getFavouritePaths() {
        return favouritePaths;
    }

    public void setFavouritePaths(List<Path> favouritePaths) {
        this.favouritePaths = favouritePaths;
    }
}
