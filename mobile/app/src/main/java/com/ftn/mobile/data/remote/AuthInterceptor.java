package com.ftn.mobile.data.remote;

import com.ftn.mobile.App;
import com.ftn.mobile.data.local.TokenStorage;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        // get JWT from SharedPreferences
        String token = TokenStorage.get(App.get());

        // if no token (for login/register), proceed normally
        if (token == null || token.isEmpty()) {
            return chain.proceed(original);
        }

        // add Authorization header
        Request requestWithAuth = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(requestWithAuth);
    }
}
