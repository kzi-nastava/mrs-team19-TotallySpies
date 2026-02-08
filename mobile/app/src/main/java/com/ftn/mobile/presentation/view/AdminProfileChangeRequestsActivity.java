package com.ftn.mobile.presentation.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.RetrofitClient;
import com.ftn.mobile.data.remote.api.AdminApi;
import com.ftn.mobile.data.remote.dto.ProfileChangeRequestDTO;
import com.ftn.mobile.presentation.adapter.ProfileChangeRequestAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProfileChangeRequestsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProfileChangeRequestAdapter adapter;
    private AdminApi api;

    private TextView tvEmptyMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_change_requests);

        recyclerView = findViewById(R.id.recyclerViewAdminRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        api = RetrofitClient.getRetrofit().create(AdminApi.class);

        findViewById(R.id.btnBackRequests).setOnClickListener(v -> finish());

        loadPendingRequests();
    }

    private void loadPendingRequests() {
        api.getPendingRequests().enqueue(new Callback<List<ProfileChangeRequestDTO>>() {
            @Override
            public void onResponse(Call<List<ProfileChangeRequestDTO>> call, Response<List<ProfileChangeRequestDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProfileChangeRequestDTO> requests = response.body();

                    if (requests.isEmpty()) {
                        showEmptyView(true);
                    } else {
                        showEmptyView(false);
                        adapter = new ProfileChangeRequestAdapter(requests, api);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(AdminProfileChangeRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProfileChangeRequestDTO>> call, Throwable t) {
                Toast.makeText(AdminProfileChangeRequestsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyView(boolean isEmpty) {
        if (isEmpty) {
            tvEmptyMessage.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            tvEmptyMessage.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }
    }
}