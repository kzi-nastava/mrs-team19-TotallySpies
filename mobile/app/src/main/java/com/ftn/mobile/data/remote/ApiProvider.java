package com.ftn.mobile.data.remote;

//import com.ftn.mobile.data.remote.api.AuthApi;
import com.ftn.mobile.data.remote.api.DriverApi;

public final class ApiProvider {
    //returns every api needed
    //private static AuthApi authApi;
    private static DriverApi driverApi;

    private ApiProvider() {}

//    public static AuthApi auth() {
//        if (authApi == null) {
//            authApi = RetrofitClient.getRetrofit().create(AuthApi.class);
//        }
//        return authApi;
//    }

    public static DriverApi driver() {
        if (driverApi == null) {
            driverApi = RetrofitClient.getRetrofit().create(DriverApi.class);
        }
        return driverApi;
    }
}