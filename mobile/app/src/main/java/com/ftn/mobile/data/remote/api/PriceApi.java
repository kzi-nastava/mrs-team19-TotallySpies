package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.model.VehiclePricing;
import com.ftn.mobile.data.remote.dto.UpdatePriceDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface PriceApi {
    @GET("api/v1/prices")
    Call<List<VehiclePricing>> getCurrentPrices();

    @PUT("api/v1/prices")
    Call<Void> updatePrice(@Body UpdatePriceDTO dto);
}