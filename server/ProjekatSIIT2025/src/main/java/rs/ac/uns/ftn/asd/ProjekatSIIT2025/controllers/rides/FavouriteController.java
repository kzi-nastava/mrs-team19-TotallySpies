package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.dto.rides.FavouriteRideDTO;
import rs.ac.uns.ftn.asd.ProjekatSIIT2025.services.FavouriteService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favourites")
public class FavouriteController {

    @Autowired
    FavouriteService favouriteService;

    @PreAuthorize("hasRole('PASSENGER')")
    @PostMapping("/{rideId}")
    public void addToFavourites(@PathVariable Long rideId, Authentication auth) {
        favouriteService.addToFavourites(rideId, auth.getName());
    }

    @PreAuthorize("hasRole('PASSENGER')")
    @DeleteMapping("/{rideId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromFavourites(@PathVariable Long rideId, Authentication auth) {
        favouriteService.removeFromFavourites(rideId, auth.getName());
    }

    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping
    public List<FavouriteRideDTO> getFavourites(Authentication auth) {
        return favouriteService.getFavourites(auth.getName());
    }
}