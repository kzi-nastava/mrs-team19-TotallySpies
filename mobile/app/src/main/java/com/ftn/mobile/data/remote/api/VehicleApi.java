package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.CreateRideRequestDTO;
import com.ftn.mobile.data.remote.dto.CreateRideResponseDTO;
import com.ftn.mobile.data.remote.dto.ScheduledRideDTO;
import com.ftn.mobile.data.remote.dto.VehicleDisplayInfoDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VehicleApi {
    @GET("api/v1/vehicles/active")
    Call<List<VehicleDisplayInfoDTO>> getAllVehicles();

}
