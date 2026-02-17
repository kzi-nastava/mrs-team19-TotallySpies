package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.CreateRideRequestDTO;
import com.ftn.mobile.data.remote.dto.CreateRideResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RideApi {
    @POST("api/v1/rides/create")
    Call<CreateRideResponseDTO> createRide(@Body CreateRideRequestDTO request);
}
