package com.ftn.mobile.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.auth.VerifyEmailDTO;
import com.ftn.mobile.utils.DialogBox;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.verify_email_scroll_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText etEmail = findViewById(R.id.etVerifyEmail);
        Button btnVerifyEmail = findViewById(R.id.btnVerifyEmail);
        btnVerifyEmail.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                DialogBox.showDialog(VerifyEmailActivity.this,"Invalid input!", "Please enter a valid email address.");
                return;
            }
            VerifyEmailDTO request = new VerifyEmailDTO(email);
            ApiProvider.forgotPassword().verifyEmail(request).enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                   if(response.code() == 200 && response.body() != null){
                       DialogBox.showDialog(VerifyEmailActivity.this, "Verification", "Email sent for verification");
                       Intent intent = new Intent(VerifyEmailActivity.this, VerifyOtpActivity.class);
                       intent.putExtra("email", email);
                       startActivity(intent);
                   }
                   else if(response.code() == 404){
                       DialogBox.showDialog(VerifyEmailActivity.this, "Error", "Email not found");
                   }
                   else{
                       DialogBox.showDialog(VerifyEmailActivity.this, "Error", "Email verification failed");
                   }

                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    DialogBox.showDialog(VerifyEmailActivity.this,"Network error",throwable.getMessage());
                }
            });
        });

    }
}