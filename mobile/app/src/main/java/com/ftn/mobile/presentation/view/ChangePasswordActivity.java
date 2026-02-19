package com.ftn.mobile.presentation.view;

import android.content.Intent;
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
import com.ftn.mobile.data.remote.dto.auth.ChangedPasswordDTO;
import com.ftn.mobile.utils.DialogBox;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_password_scroll_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText etPassword = findViewById(R.id.etChangePassword);
        EditText etConfirmPassword = findViewById(R.id.etChangePasswordConfirm);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            String confirmedPassword = etConfirmPassword.getText().toString().trim();
            if(password == null || confirmedPassword == null){
                DialogBox.showDialog(ChangePasswordActivity.this, "Invalid input", "Please fill all the fields!");
                return;
            }
            if(!(password.equals(confirmedPassword))){
                DialogBox.showDialog(ChangePasswordActivity.this, "Invalid input", "Password and confirmed password must be equal");
                return;
            }
            ChangedPasswordDTO request = new ChangedPasswordDTO(password, email, confirmedPassword);
            ApiProvider.forgotPassword().changePassword(request).enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200 && response.body() != null){
                        DialogBox.showDialog(ChangePasswordActivity.this, "Password changed", "Password has changed!");

                    }
                    else if(response.code() == 417){
                        DialogBox.showDialog(ChangePasswordActivity.this, "Error", "Please enter the password again!");
                    }
                    else{
                        DialogBox.showDialog(ChangePasswordActivity.this, "Error", "OTP verification failed.");
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    DialogBox.showDialog(ChangePasswordActivity.this, "Network", throwable.getMessage());
                }
            });
        });

    }
}