package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import java.util.ArrayList;

public class Passenger extends User {
    private ArrayList<Ride> rides;
    private ArrayList<Path> favouritePaths;

    public Passenger(ArrayList<Ride> rides, ArrayList<Path> favouritePaths) {
        this.rides = rides;
        this.favouritePaths = favouritePaths;
    }

    public ArrayList<Ride> getRides() {
        return rides;
    }

    public void setRides(ArrayList<Ride> rides) {
        this.rides = rides;
    }

    public ArrayList<Path> getFavouritePaths() {
        return favouritePaths;
    }

    public void setFavouritePaths(ArrayList<Path> favouritePaths) {
        this.favouritePaths = favouritePaths;
    }
}
