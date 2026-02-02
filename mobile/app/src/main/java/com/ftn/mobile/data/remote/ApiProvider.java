package com.ftn.mobile.data.remote;

import com.ftn.mobile.data.remote.api.AuthApi;

public final class ApiProvider {
    //returns every api needed
    private static AuthApi authApi;

    private ApiProvider() {}

    public static AuthApi auth() {
        if (authApi == null) {
            authApi = RetrofitClient.getRetrofit().create(AuthApi.class);
        }
        return authApi;
    }
}
