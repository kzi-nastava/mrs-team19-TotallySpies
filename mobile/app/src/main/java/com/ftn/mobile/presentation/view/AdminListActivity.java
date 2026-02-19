package com.ftn.mobile.presentation.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.CarInfo;
import com.ftn.mobile.data.model.Driver;
import com.ftn.mobile.data.model.User;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.AdminUserDTO;
import com.ftn.mobile.data.remote.dto.BlockRequestDTO;
import com.ftn.mobile.presentation.adapter.DriverAdapter;
import com.ftn.mobile.presentation.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminListActivity extends AppCompatActivity {
    private RecyclerView rv;
    private String type;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        rv = findViewById(R.id.recyclerViewAdmin);
        rv.setLayoutManager(new LinearLayoutManager(this));
        title = findViewById(R.id.tvMangeTitle);

        type = getIntent().getStringExtra("TYPE");

        if ("USERS".equals(type)) {
            title.setText("Manage Passengers");
        } else if ("DRIVERS".equals(type)) {
            title.setText("Manage Drivers");
        }

        loadDataFromServer();

        findViewById(R.id.btnBackAdmin).setOnClickListener(v -> finish());
    }
    private void loadDataFromServer() {
        Call<List<AdminUserDTO>> call;
        if ("USERS".equals(type)) {
            call = ApiProvider.admin().getPassengers();
        } else {
            call = ApiProvider.admin().getDrivers();
        }

        call.enqueue(new Callback<List<AdminUserDTO>>() {
            @Override
            public void onResponse(Call<List<AdminUserDTO>> call, Response<List<AdminUserDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    setupAdapter(response.body());
                } else {
                    Toast.makeText(AdminListActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminUserDTO>> call, Throwable t) {
                Toast.makeText(AdminListActivity.this, "Server unreachable", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupAdapter(List<AdminUserDTO> users) {
        UserAdapter adapter = new UserAdapter(users, user -> {
            if (user.isBlocked()) {
                unblockUser(user.getId());
            } else {
                showBlockDialog(user);
            }
        });
        rv.setAdapter(adapter);
    }

    private void unblockUser(Long userId) {
        ApiProvider.admin().unblockUser(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(AdminListActivity.this, "User unblocked!", Toast.LENGTH_SHORT).show();
                loadDataFromServer(); // Osveži listu
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void showBlockDialog(AdminUserDTO user) {
        EditText etReason = new EditText(this);
        etReason.setHint("Enter reason for blocking");

        new AlertDialog.Builder(this)
                .setTitle("Block " + user.getName())
                .setMessage("Please provide a reason:")
                .setView(etReason)
                .setPositiveButton("Block", (dialog, which) -> {
                    String reason = etReason.getText().toString();
                    if (!reason.isEmpty()) {
                        blockUser(user.getId(), reason);
                    } else {
                        Toast.makeText(this, "Reason is required!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void blockUser(Long userId, String reason) {
        BlockRequestDTO request = new BlockRequestDTO();
        request.setReason(reason);

        ApiProvider.admin().blockUser(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(AdminListActivity.this, "User blocked!", Toast.LENGTH_SHORT).show();
                loadDataFromServer();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}

