package com.ftn.mobile.data.remote;

import com.ftn.mobile.data.remote.api.AdminApi;
import com.ftn.mobile.data.remote.api.AuthApi;

import com.ftn.mobile.data.remote.api.DriverApi;
import com.ftn.mobile.data.remote.api.FavouriteApi;
import com.ftn.mobile.data.remote.api.ForgotPasswordApi;
import com.ftn.mobile.data.remote.api.PriceApi;
import com.ftn.mobile.data.remote.api.ReviewApi;
import com.ftn.mobile.data.remote.api.RideApi;
import com.ftn.mobile.data.remote.api.UserApi;
import com.ftn.mobile.data.remote.api.VehicleApi;

public final class ApiProvider {
    //returns every api needed
    private static AuthApi authApi;

    private static ForgotPasswordApi forgotPasswordApi;

    private static UserApi userApi;

    private static DriverApi driverApi;

    private static AdminApi adminApi;

    private static PriceApi priceApi;

    private static RideApi rideApi;

    private static FavouriteApi favouriteApi;

    private static VehicleApi vehicleApi;
    private static ReviewApi reviewApi;

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

    public static PriceApi price() {
        if (priceApi == null){
            priceApi = RetrofitClient.getRetrofit().create(PriceApi.class);
        }
        return priceApi;
    }

    public static RideApi ride() {
        if (rideApi == null){
            rideApi = RetrofitClient.getRetrofit().create(RideApi.class);
        }
        return rideApi;
    }

    public static FavouriteApi favourite() {
        if (favouriteApi == null){
            favouriteApi = RetrofitClient.getRetrofit().create(FavouriteApi.class);
        }
        return favouriteApi;
    }
    public static VehicleApi vehicle() {
        if (vehicleApi == null){
            vehicleApi = RetrofitClient.getRetrofit().create(VehicleApi.class);
        }
        return vehicleApi;
    }

    public static ReviewApi review() {
        if (reviewApi == null){
            reviewApi = RetrofitClient.getRetrofit().create(ReviewApi.class);
        }
        return reviewApi;
    }
}

