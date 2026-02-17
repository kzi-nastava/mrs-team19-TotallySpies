package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.FavouriteRideDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FavouriteApi {
    @GET("/api/v1/favourites")
    Call<List<FavouriteRideDTO>> getFavourites();

    @POST("/api/v1/favourites/{rideId}")
    Call<Void> addToFavourites(@Path("rideId") Long rideId);

    @DELETE("/api/v1/favourites/{id}")
    Call<Void> removeFromFavourites(@Path("id") Long id);
}
