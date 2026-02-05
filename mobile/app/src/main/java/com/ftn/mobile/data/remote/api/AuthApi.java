package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.UserLoginRequestDTO;
import com.ftn.mobile.data.remote.dto.UserTokenStateDTO;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface AuthApi {
    @POST("api/v1/auth/login")
    Call<UserTokenStateDTO> login(@Body UserLoginRequestDTO request);

    @Multipart
    @POST("api/v1/auth/register")
    Call<ResponseBody> register(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("confirmedPassword") RequestBody confirmedPassword,
            @Part("name") RequestBody name,
            @Part("lastName") RequestBody lastName,
            @Part("address") RequestBody address,
            @Part("phoneNumber") RequestBody phoneNumber,
            @Part MultipartBody.Part profilePicture
    );

    /*@POST("api/v1/auth/activate")
    Call<String> activate(@Query String token);*/
}
