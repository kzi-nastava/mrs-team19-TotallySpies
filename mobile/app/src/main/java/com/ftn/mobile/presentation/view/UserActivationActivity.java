package com.ftn.mobile.presentation.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.utils.DialogBox;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_activation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Uri data = getIntent().getData();
        if (data == null) {
            DialogBox.showDialog(this, "Error", "Missing activation link data.");
            finish();
            return;
        }
        String token = data.getQueryParameter("token");
        if (token == null || token.isEmpty()) {
            DialogBox.showDialog(this, "Error", "Missing activation token.");
            finish();
            return;
        }
        ApiProvider.auth().activate(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    DialogBox.showDialog(UserActivationActivity.this,
                            "Activated", "Account activated. You can log in now.");
                    startActivity(new Intent(UserActivationActivity.this, LoginActivity.class));
                    finish();
                } else if (response.code() == 400) {
                    DialogBox.showDialog(UserActivationActivity.this,
                            "Error", "Invalid/used/expired token.");
                } else {
                    DialogBox.showDialog(UserActivationActivity.this,
                            "Error", "Activation failed");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogBox.showDialog(UserActivationActivity.this,
                        "Network error", t.getMessage());
            }
        });
    }
}