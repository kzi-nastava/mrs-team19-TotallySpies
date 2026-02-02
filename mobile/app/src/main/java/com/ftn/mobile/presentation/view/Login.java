package com.ftn.mobile.presentation.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

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

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            UserLoginRequestDTO req = new UserLoginRequestDTO(email, pass);

            ApiProvider.auth().login(req).enqueue(new Callback<UserTokenStateDTO>() {
                @Override
                public void onResponse(Call<UserTokenStateDTO> call, Response<UserTokenStateDTO> response) {
                    if (response.code() == 200 && response.body() != null) {
                        String token = response.body().getAccessToken();
                        TokenStorage.save(Login.this, token);
                        Toast.makeText(Login.this, "Login OK", Toast.LENGTH_SHORT).show();
                        // startActivity(new Intent(Login.this, HomeActivity.class));
                        // finish();
                    } else if (response.code() == 401) {
                        Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 403) {
                        Toast.makeText(Login.this, "Account not activated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserTokenStateDTO> call, Throwable t) {
                    Toast.makeText(Login.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

    }
}