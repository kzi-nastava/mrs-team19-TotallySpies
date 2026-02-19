package com.ftn.mobile.data.remote.api;

import com.ftn.mobile.data.remote.dto.ChatMessageDTO;
import com.ftn.mobile.data.remote.dto.UserChatDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatApi {
    @GET("api/v1/chat/history/{userId}")
    Call<List<ChatMessageDTO>> getChatHistory(@Path("userId") long userId);

    @GET("api/v1/chat/users")
    Call<List<UserChatDTO>> getChatUsers();
}