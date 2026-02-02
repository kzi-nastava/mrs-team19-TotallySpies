package com.ftn.mobile.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.ftn.mobile.BuildConfig;


public class RetrofitClient {
    //sets the base server address, chooses json converter, adds interceptor, ensures one shared instance
    //for whole app, all api calls go through it
    //retrofit combines base url with endpoint paths
    private static Retrofit retrofit;
    private RetrofitClient(){};
    public static Retrofit getRetrofit(){
        if(retrofit == null){
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new AuthInterceptor())
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
