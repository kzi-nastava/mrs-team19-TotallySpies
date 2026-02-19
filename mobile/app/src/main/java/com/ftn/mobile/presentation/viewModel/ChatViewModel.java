package com.ftn.mobile.presentation.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.ChatMessageDTO;
import com.ftn.mobile.data.remote.dto.ChatMessageDTO;
import com.ftn.mobile.data.remote.dto.UserChatDTO;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<ChatMessageDTO>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<UserChatDTO>> users = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    private StompClient stompClient;
    private CompositeDisposable disposable = new CompositeDisposable();
    private Gson gson = new Gson();
    private Long currentUserId;
    private boolean isAdmin;
    private Long otherUserId;
    private Disposable topicDisposable;

    public LiveData<List<ChatMessageDTO>> getMessages() { return messages; }
    public LiveData<List<UserChatDTO>> getUsers() { return users; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void init(Long userId, boolean admin) {
        this.currentUserId = userId;
        this.isAdmin = admin;
        connectWebSocket();
        if (admin) {
            loadUsers();
        } else {
            loadHistory(userId);
        }
    }
    public void init(long userId, boolean admin, Long otherUserId) {
        this.currentUserId = userId;
        this.isAdmin = admin;
        this.otherUserId = otherUserId;
        connectWebSocket();
        if (admin) {
            loadUsers();
            if (otherUserId != null) {
                loadHistory(otherUserId);
            }
        } else {
            loadHistory(userId);
        }
    }

    public void setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
        if (otherUserId != null) {
            loadHistory(otherUserId);
        }
    }
    private void addMessage(ChatMessageDTO message) {
        List<ChatMessageDTO> current = messages.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(message);
        messages.postValue(current);
    }

    private void connectWebSocket() {
        if (stompClient != null && stompClient.isConnected()) return;
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/ws-transport/websocket");
        if (topicDisposable != null) topicDisposable.dispose();
        String topicPath = isAdmin ? "/topic/admin/messages" : "/topic/user/" + currentUserId;
        topicDisposable = stompClient.topic(topicPath).subscribe(msg -> {
            ChatMessageDTO message = gson.fromJson(msg.getPayload(), ChatMessageDTO.class);
            if (isAdmin) {
                if (otherUserId != null && (
                        message.getSenderId().equals(otherUserId) ||
                                (message.getSenderId().equals(currentUserId) && message.getReceiverId().equals(otherUserId))
                )) {
                    addMessage(message);
                }
            } else {
                addMessage(message);
            }
        }, throwable -> Log.e("STOMP", "Error", throwable));

        disposable.add(topicDisposable);
        stompClient.connect();
    }
    public void loadHistory(Long userId) {
        ApiProvider.chat().getChatHistory(userId).enqueue(new Callback<List<ChatMessageDTO>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDTO>> call, Response<List<ChatMessageDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messages.postValue(response.body());
                } else {
                    toastMessage.postValue("Failed to load messages");
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessageDTO>> call, Throwable t) {
                toastMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    private void loadUsers() {
        ApiProvider.chat().getChatUsers().enqueue(new Callback<List<UserChatDTO>>() {
            @Override
            public void onResponse(Call<List<UserChatDTO>> call, Response<List<UserChatDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users.postValue(response.body());
                } else {
                    toastMessage.postValue("Failed to load users");
                }
            }

            @Override
            public void onFailure(Call<List<UserChatDTO>> call, Throwable t) {
                toastMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void sendMessage(String content, Long receiverId) {
        ChatMessageDTO dto = new ChatMessageDTO(currentUserId, receiverId, content);
        String destination = isAdmin ? "/app/chat.adminResponse" : "/app/chat.sendMessage";
        String json = gson.toJson(dto);
        Log.d("CHAT_SEND", "Sending to " + destination + ": " + json);
        disposable.add(stompClient.send(destination, json).subscribe(() -> {
            Log.d("CHAT_SEND", "Message sent successfully");
        }, throwable -> {
            Log.e("CHAT_SEND", "Error sending message", throwable);
            toastMessage.postValue("Failed to send: " + throwable.getMessage());
        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (stompClient != null) stompClient.disconnect();
        disposable.clear();
    }
}