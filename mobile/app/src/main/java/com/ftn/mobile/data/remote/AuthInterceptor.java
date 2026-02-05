package com.ftn.mobile.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import com.ftn.mobile.App;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences sp = App.get().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = sp.getString("jwt_token", "");

        Request.Builder builder = chain.request().newBuilder();

        if (!token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        return chain.proceed(builder.build());
    }
}