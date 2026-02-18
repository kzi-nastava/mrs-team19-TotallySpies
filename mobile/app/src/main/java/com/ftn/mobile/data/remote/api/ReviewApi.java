package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.ReviewRequestDTO;
import com.ftn.mobile.data.remote.dto.ReviewResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewApi {
    @POST("api/v1/reviews/{rideId}/driver")
    Call<ReviewResponseDTO> rateDriver(@Path("rideId") long rideId, @Body ReviewRequestDTO request);

    @POST("api/v1/reviews/{rideId}/vehicle")
    Call<ReviewResponseDTO> rateVehicle(@Path("rideId") long rideId, @Body ReviewRequestDTO request);
}
