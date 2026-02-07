package com.ftn.mobile.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.ftn.mobile.R;
import com.ftn.mobile.data.local.TokenStorage;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.UserLoginRequestDTO;
import com.ftn.mobile.data.remote.dto.UserTokenStateDTO;
import com.ftn.mobile.utils.DialogBox;
import com.ftn.mobile.data.local.UserRoleManger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_scroll_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, VerifyEmailActivity.class);
            startActivity(intent);

        });
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                //Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                DialogBox.showDialog(LoginActivity.this, "Invalid input!", "Please fill all the fields.");
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                DialogBox.showDialog(LoginActivity.this, "Invalid input!", "Please enter a valid email address.");
                return;
            }
            if(password.length() < 8){
                DialogBox.showDialog(LoginActivity.this, "Invalid input!", "Password is required in format of at least 8 characters!");
                return;
            }
            UserLoginRequestDTO req = new UserLoginRequestDTO(email, password);

            ApiProvider.auth().login(req).enqueue(new Callback<UserTokenStateDTO>() {
                @Override
                public void onResponse(Call<UserTokenStateDTO> call, Response<UserTokenStateDTO> response) {
                    if (response.code() == 200 && response.body() != null) {
                        String token = response.body().getAccessToken();
                        TokenStorage.save(LoginActivity.this, token);
                        UserRoleManger.updateRole(token);
                        DialogBox.showDialog(LoginActivity.this, "Logged in", "Successfully logged!");
                        // startActivity(new Intent(Login.this, HomeActivity.class));
                    } else if (response.code() == 401) {
                        DialogBox.showDialog(LoginActivity.this, "Error", "Invalid email or password");
                    } else if (response.code() == 403) {
                        DialogBox.showDialog(LoginActivity.this, "Error", "Account not activated. Check your email.");
                    } else {
                        DialogBox.showDialog(LoginActivity.this, "Error", "Login failed.");
                    }
                }
                @Override
                public void onFailure(Call<UserTokenStateDTO> call, Throwable t) {
                    DialogBox.showDialog(LoginActivity.this, "Network error", t.getMessage());
                }
            });
        });

    }
}