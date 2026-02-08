package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.ChangedPasswordDTO;
import com.ftn.mobile.data.remote.dto.VerifyEmailDTO;
import com.ftn.mobile.data.remote.dto.VerifyOtpDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ForgotPasswordApi {
    @POST("api/v1/forgot-password/verify-email")
    Call<ResponseBody> verifyEmail(@Body VerifyEmailDTO request);

    @POST("api/v1/forgot-password/verify-otp")
    Call<ResponseBody> verifyOtp(@Body VerifyOtpDTO request);

    @POST("api/v1/forgot-password/change-password")
    Call<ResponseBody> changePassword(@Body ChangedPasswordDTO request);
}
