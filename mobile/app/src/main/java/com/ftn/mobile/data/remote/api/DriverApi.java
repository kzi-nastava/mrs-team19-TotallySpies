package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.DriverActivityResponseDTO;
import com.ftn.mobile.data.remote.dto.DriverBlockedStatusDTO;
import com.ftn.mobile.data.remote.dto.VehicleInfoResponseDTO;

import com.ftn.mobile.data.remote.dto.DriverRideHistoryDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverApi {
    // GET /api/v1/drivers/activity
    @GET("api/v1/drivers/activity")
    Call<DriverActivityResponseDTO> getActivity();

    // GET /api/v1/drivers/vehicle-info
    @GET("api/v1/drivers/vehicle-info")
    Call<VehicleInfoResponseDTO>  getVehicleInfo();

    @GET(value = "api/v1/drivers/history")
    Call<List<DriverRideHistoryDTO>> getDriverHistory(
            @Query("from") String from,
            @Query("to") String to
    );

    @GET("api/v1/drivers/blocked-status")
    Call<DriverBlockedStatusDTO> getBlockedStatus();
}
