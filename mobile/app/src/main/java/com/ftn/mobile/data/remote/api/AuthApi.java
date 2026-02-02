package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.UserLoginRequestDTO;
import com.ftn.mobile.data.remote.dto.UserTokenStateDTO;

import kotlin.ParameterName;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {
    @POST("api/v1/auth/login")
    Call<UserTokenStateDTO> login(@Body UserLoginRequestDTO request);

    /*@POST("api/v1/auth/register")
    Call<UserRegisterRequestDTO> register( @Body UserRegisterRequestDTO request);

    @POST("api/v1/auth/activate")
    Call<String> activate(@Query String token);*/
}
