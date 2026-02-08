package com.ftn.mobile.data.remote;

import com.ftn.mobile.data.remote.api.AdminApi;
import com.ftn.mobile.data.remote.api.AuthApi;

import com.ftn.mobile.data.remote.api.DriverApi;
import com.ftn.mobile.data.remote.api.ForgotPasswordApi;
import com.ftn.mobile.data.remote.api.UserApi;

public final class ApiProvider {
    //returns every api needed
    private static AuthApi authApi;

    private static ForgotPasswordApi forgotPasswordApi;

    private static UserApi userApi;

    private static DriverApi driverApi;

    private static AdminApi adminApi;

    private ApiProvider() {}

    public static AuthApi auth() {
        if (authApi == null) {
            authApi = RetrofitClient.getRetrofit().create(AuthApi.class);
        }
        return authApi;
    }
    public static ForgotPasswordApi forgotPassword(){
        if(forgotPasswordApi == null){
            forgotPasswordApi = RetrofitClient.getRetrofit().create(ForgotPasswordApi.class);
        }
        return forgotPasswordApi;
    }

    public static UserApi user() {
        if (userApi == null) {
            userApi = RetrofitClient.getRetrofit().create(UserApi.class);
        }
        return userApi;
    }

    public static DriverApi driver() {
        if (driverApi == null) {
            driverApi = RetrofitClient.getRetrofit().create(DriverApi.class);
        }
        return driverApi;
    }

    public static AdminApi admin() {
        if (adminApi == null){
            adminApi = RetrofitClient.getRetrofit().create(AdminApi.class);
        }
        return adminApi;
    }
}

