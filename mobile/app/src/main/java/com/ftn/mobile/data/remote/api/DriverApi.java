package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.DriverActivityResponseDTO;
import com.ftn.mobile.data.remote.dto.VehicleInfoResponseDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DriverApi {
    // GET /api/v1/drivers/activity
    @GET("api/v1/drivers/activity")
    Call<DriverActivityResponseDTO> getActivity();

    // GET /api/v1/drivers/vehicle-info
    @GET("api/v1/drivers/vehicle-info")
    Call<VehicleInfoResponseDTO>  getVehicleInfo();
}
