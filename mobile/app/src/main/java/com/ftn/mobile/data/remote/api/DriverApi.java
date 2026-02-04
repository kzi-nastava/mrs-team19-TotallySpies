package com.ftn.mobile.data.remote.api;


import com.ftn.mobile.data.remote.dto.DriverRideHistoryDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverApi {

    @GET(value = "api/v1/drivers/{id}/ride")
    Call<List<DriverRideHistoryDTO>> getDriverHistory(
            @Path("id") Long id,
            @Query("from") String from,
            @Query("to") String to
    );
}
