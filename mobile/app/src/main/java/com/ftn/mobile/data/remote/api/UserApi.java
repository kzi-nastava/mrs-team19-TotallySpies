package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.auth.ChangePasswordRequestDTO;
import com.ftn.mobile.data.remote.dto.ReportResponseDTO;
import com.ftn.mobile.data.remote.dto.UserProfileResponseDTO;
import com.ftn.mobile.data.remote.dto.UserProfileUpdateRequestDTO;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideHistoryDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    @GET("api/v1/users/history")
    Call<List<PassengerRideHistoryDTO>> getPassengerRideHistory(
            @Query("sortBy") String sortBy,
            @Query("sortDirection") String sortDirection,
            @Query("from") String from, //ISO date time
            @Query("to") String to
    );

    // GET /api/v1/users/profile
    @GET("api/v1/users/profile")
    Call<UserProfileResponseDTO> getProfile();

    // PUT /api/v1/users/profile
    @PUT("api/v1/users/profile")
    Call<Void> updateProfile(@Body UserProfileUpdateRequestDTO request);

    // PUT /api/v1/users/profile/image
    @Multipart
    @PUT("api/v1/users/profile/image")
    Call<Void> uploadImage(@Part MultipartBody.Part image);

    // PUT /api/v1/users/change-password
    @PUT("api/v1/users/change-password")
    Call<Void> changePassword(@Body ChangePasswordRequestDTO request);

    // GET /api/v1/users/image/{filename}
    @GET("api/v1/users/image/{filename}")
    Call<ResponseBody> getProfileImage(@Path("filename") String filename);

    // GET /api/v1/users/report
    @GET("api/v1/users/report")
    Call<ReportResponseDTO> getReport(
            @Query("from") String from, // ISO format: yyyy-MM-dd'T'HH:mm:ss
            @Query("to") String to,
            @Query("targetEmail") String targetEmail
    );
}
