package com.ftn.mobile.presentation.viewModel;


import static androidx.lifecycle.AndroidViewModel_androidKt.getApplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.NotificationDTO;
import com.ftn.mobile.presentation.view.MainActivity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class NotificationViewModel extends ViewModel {
    private MutableLiveData<List<NotificationDTO>> notifications = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Integer> unreadCount = new MutableLiveData<>(0);
    private MutableLiveData<String> toastMessage = new MutableLiveData<>();

    private StompClient stompClient;
    private Gson gson = new Gson();
    private long currentUserId;
    private Context context;

    public LiveData<List<NotificationDTO>> getNotifications() { return notifications; }
    public LiveData<Integer> getUnreadCount() { return unreadCount; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void init(long userId, Context context) {
        this.currentUserId = userId;
        this.context = context.getApplicationContext();
        loadNotifications();
        connectWebSocket();
    }

    private void connectWebSocket() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/ws-transport/websocket");
        stompClient.topic("/topic/notifications/" + currentUserId).subscribe(msg -> {
            NotificationDTO notification = gson.fromJson(msg.getPayload(), NotificationDTO.class);
            // Add to list and update unread count
            List<NotificationDTO> current = notifications.getValue();
            if (current == null) current = new ArrayList<>();
            current.add(0, notification); // newest first
            notifications.postValue(current);
            updateUnreadCount();
        });
        stompClient.connect();
    }

    private void loadNotifications() {
        ApiProvider.notification().getNotifications().enqueue(new Callback<List<NotificationDTO>>() {
            @Override
            public void onResponse(Call<List<NotificationDTO>> call, Response<List<NotificationDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notifications.postValue(response.body());
                    updateUnreadCount();
                } else {
                    toastMessage.postValue("Failed to load notifications");
                }
            }

            @Override
            public void onFailure(Call<List<NotificationDTO>> call, Throwable t) {
                toastMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    private void updateUnreadCount() {
        List<NotificationDTO> list = notifications.getValue();
        if (list != null) {
            int count = 0;
            for (NotificationDTO n : list) {
                if (!n.isRead()) count++;
            }
            unreadCount.postValue(count);
        }
    }

    public void markAsRead(NotificationDTO notification) {
        if (notification.isRead()) return;
        ApiProvider.notification().markAsRead(notification.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    notification.setRead(true);
                    List<NotificationDTO> list = notifications.getValue();
                    if (list != null) {
                        notifications.postValue(list); // trigger update
                        updateUnreadCount();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                toastMessage.postValue("Failed to mark as read");
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (stompClient != null) stompClient.disconnect();
    }

    private void showSystemNotification(NotificationDTO notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ride_channel", "Ride Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ride_channel")
                .setSmallIcon(R.drawable.ic_info)
                .setContentTitle("Ride Update")
                .setContentText(notification.getMessage())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(createPendingIntent(notification.getRideId()));
        notificationManager.notify(notification.getId().intValue(), builder.build());
    }

    private PendingIntent createPendingIntent(Long rideId) {
        Intent intent = new Intent(context, MainActivity.class);
        if (rideId != null) {
            intent.putExtra("rideId", rideId);
            intent.putExtra("openTracking", true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}