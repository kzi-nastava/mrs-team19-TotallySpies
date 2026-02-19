package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.NotificationDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificationApi {
    @GET("api/v1/notifications")
    Call<List<NotificationDTO>> getNotifications();

    @PUT("api/v1/notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") Long id);
}
