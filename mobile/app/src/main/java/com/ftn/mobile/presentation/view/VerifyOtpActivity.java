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
import com.ftn.mobile.data.remote.dto.auth.VerifyOtpDTO;
import com.ftn.mobile.utils.DialogBox;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.verify_otp_scroll_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText[] otpFields = {
                findViewById(R.id.otp1),
                findViewById(R.id.otp2),
                findViewById(R.id.otp3),
                findViewById(R.id.otp4),
                findViewById(R.id.otp5),
                findViewById(R.id.otp6)
        };

        Button btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnVerifyOtp.setOnClickListener(v->{
            StringBuilder otpBuilder = new StringBuilder();
            for(EditText otp : otpFields){
                otpBuilder.append(otp.getText().toString());
            }
            String otpValue = otpBuilder.toString();
            int otp = Integer.parseInt(otpValue);
            Intent intent = getIntent();
            String email = intent.getStringExtra("email");
            if(otpValue.length() != 6){
                DialogBox.showDialog(VerifyOtpActivity.this, "Invalid input", "OTP invalid format!");
            }
            VerifyOtpDTO request = new VerifyOtpDTO(email, otp);
            ApiProvider.forgotPassword().verifyOtp(request).enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code() == 200 && response.body() != null){
                        DialogBox.showDialog(VerifyOtpActivity.this, "Verified", "OTP verified!");
                        Intent intent = new Intent(VerifyOtpActivity.this, ChangePasswordActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                    else if(response.code() == 404){
                        DialogBox.showDialog(VerifyOtpActivity.this, "Error", "Email not found!");
                    }
                    else if(response.code() == 400){
                        DialogBox.showDialog(VerifyOtpActivity.this, "Error", "Invalid OTP!");
                    }
                    else if(response.code() == 417){
                        DialogBox.showDialog(VerifyOtpActivity.this, "Error", "OTP has expired!");
                    }
                    else{
                        DialogBox.showDialog(VerifyOtpActivity.this, "Error", "OTP verification failed.");
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    DialogBox.showDialog(VerifyOtpActivity.this, "Network", throwable.getMessage());
                }
            });

        });

    }
}