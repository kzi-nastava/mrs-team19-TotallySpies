package com.ftn.mobile.data.remote;
import com.ftn.mobile.data.remote.api.AuthApi;
import com.ftn.mobile.data.remote.api.ForgotPasswordApi;
public final class ApiProvider {
    //returns every api needed
    private static AuthApi authApi;
    private static ForgotPasswordApi forgotPasswordApi;

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
}
