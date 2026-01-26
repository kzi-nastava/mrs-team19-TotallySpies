package rs.ac.uns.ftn.asd.ProjekatSIIT2025.controllers.rides;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/{rideId}")
    public void addToFavourites(@PathVariable Long rideId, Authentication auth) {
        favouriteService.addToFavourites(rideId, auth.getName());
    }

    @DeleteMapping("/{rideId}")
    public void removeFromFavourites(@PathVariable Long rideId, Authentication auth) {
        favouriteService.removeFromFavourites(rideId, auth.getName());
    }

    @GetMapping
    public List<FavouriteRideDTO> getFavourites(Authentication auth) {
        return favouriteService.getFavourites(auth.getName());
    }
}