package rs.ac.uns.ftn.asd.ProjekatSIIT2025.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.FavouriteRideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Passenger;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.model.Ride;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.PassengerRepository;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.repositories.RideRepository;

import java.util.List;

@Service
@Transactional
public class FavouriteService {
    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    RideRepository rideRepository;

    public void addToFavourites(Long rideId, String email) {
        Passenger p = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Passenger not found: " + email));

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ride not found"));

        if (!p.getFavouriteRides().contains(ride)) {
            p.getFavouriteRides().add(ride);
            passengerRepository.save(p);
        }
    }

    public void removeFromFavourites(Long rideId, String email) {
        Passenger p = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Passenger not found: " + email));

        p.getFavouriteRides()
                .removeIf(r -> r.getId().equals(rideId));

        passengerRepository.save(p);
    }

    public List<FavouriteRideDTO> getFavourites(String email) {
        Passenger p = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Passenger not found: " + email));

        return p.getFavouriteRides().stream()
                .map(r -> new FavouriteRideDTO(
                        r.getId(),
                        r.getStops()
                ))
                .toList();
    }
}
