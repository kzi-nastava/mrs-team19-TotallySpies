package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.ActiveRideDTO;
import com.ftn.mobile.data.remote.dto.CreateRideRequestDTO;
import com.ftn.mobile.data.remote.dto.CreateRideResponseDTO;
import com.ftn.mobile.data.remote.dto.PassengerUpcomingRideDTO;
import com.ftn.mobile.data.remote.dto.InconsistencyReportRequestDTO;
import com.ftn.mobile.data.remote.dto.RideTrackingDTO;
import com.ftn.mobile.data.remote.dto.ScheduledRideDTO;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideDetailsResponseDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RideApi {
    @POST("api/v1/rides/create")
    Call<CreateRideResponseDTO> createRide(@Body CreateRideRequestDTO request);

    @GET("api/v1/rides/scheduled")
    Call<List<ScheduledRideDTO>> getScheduledRides();

    @PUT("api/v1/rides/{id}/end")
    Call<ScheduledRideDTO> finishRide(@Path("id") Long id);

    @GET("api/v1/rides/passenger-upcoming")
    Call<List<PassengerUpcomingRideDTO>> getPassengerUpcoming();

    @PUT("api/v1/rides/{id}/start")
    Call<ResponseBody> startRide(@Path("id") Long id);

    @GET("api/v1/rides/{id}/details")
    Call<PassengerRideDetailsResponseDTO> getRideDetails(@Path("id") Long id);
    @POST("api/v1/rides/{rideId}/inconsistency-report")
    Call<ResponseBody> reportInconsistency(@Path("rideId") long rideId, @Body InconsistencyReportRequestDTO request);
    @GET("api/v1/rides/active-tracking")
    Call<RideTrackingDTO> getActiveRide();

    @GET("api/v1/rides/{rideId}/location")
    Call<RideTrackingDTO> getRideLocation(@Path("rideId") long rideId);

    @GET("api/v1/rides/last-completed")
    Call<RideTrackingDTO> getLastCompletedRide();

    @GET("api/v1/rides/active-admin")
    Call<List<ActiveRideDTO>> getActiveRidesForAdmin();
}
