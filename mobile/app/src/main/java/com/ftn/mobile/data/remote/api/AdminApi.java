package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.CreateDriverRequestDTO;
import com.ftn.mobile.data.remote.dto.DriverActivityResponseDTO;
import com.ftn.mobile.data.remote.dto.ProfileChangeRequestDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdminApi {
    //GET /api/v1/admin/profile-change-requests
    @GET("api/v1/admin/profile-change-requests")
    Call<List<ProfileChangeRequestDTO>> getPendingRequests();

    //POST /api/v1/admin/profile-change-requests/{id}/approve
    @POST("api/v1/admin/profile-change-requests/{id}/approve")
    Call<ResponseBody> approve(@Path("id") Long id);

    //POST /api/v1/admin/profile-change-requests/{id}/reject
    @POST("api/v1/admin/profile-change-requests/{id}/reject")
    Call<ResponseBody> reject(@Path("id") Long id);

    //POST /api/v1/admin/create-driver
    @POST("api/v1/admin/create-driver")
    Call<Void> createDriver(@Body CreateDriverRequestDTO dto);

}
