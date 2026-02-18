package com.ftn.mobile.presentation.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.DriverActivationRequestDTO;
import com.ftn.mobile.utils.DialogBox;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivateDriverAccountActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private String activationToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_driver_account);

        etNewPassword = findViewById(R.id.etPasswordDriver);
        etConfirmPassword = findViewById(R.id.etConfirmPasswordDriver);
        Button btnSubmit = findViewById(R.id.btnActivateDriverAccount);

        // hvatanje tokena iz url
        Uri data = getIntent().getData();
        if (data != null && data.getQueryParameter("token") != null) {
            activationToken = data.getQueryParameter("token");
        } else {
            DialogBox.showDialog(this, "Error", "Activation link expires!");
        }

        btnSubmit.setOnClickListener(v -> submitActivation());
    }

    private void submitActivation() {
        String pass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (pass.isEmpty() || !pass.equals(confirmPass)) {
            DialogBox.showDialog(this, "Error", "Check your password, not match");
            return;
        }

        DriverActivationRequestDTO dto = new DriverActivationRequestDTO(activationToken, pass, confirmPass);

        ApiProvider.auth().activateDriver(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    DialogBox.showDialog(ActivateDriverAccountActivity.this, "Success",
                            "Your account is active! Now you can login.",
                            (dialog, which) -> {
                                Intent intent = new Intent(ActivateDriverAccountActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            });
                }
                else {
                    DialogBox.showDialog(ActivateDriverAccountActivity.this, "Error", "Activation failed.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogBox.showDialog(ActivateDriverAccountActivity.this, "Error", "Network error.");
            }
        });
    }
}