package com.ftn.mobile.presentation.view;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.UserProfileResponseDTO;
import com.ftn.mobile.data.remote.dto.UserProfileUpdateRequestDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etName, etLastName, etPhone, etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etEditName);
        etLastName = findViewById(R.id.etEditLastName);
        etPhone = findViewById(R.id.etEditPhoneNumber);
        etAddress = findViewById(R.id.etEditAddress);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadProfile();

        findViewById(R.id.btnSaveEditChanges).setOnClickListener(v -> save());
    }

    private void loadProfile() {
        ApiProvider.user().getProfile().enqueue(new Callback<UserProfileResponseDTO>() {
            @Override
            public void onResponse(Call<UserProfileResponseDTO> call, Response<UserProfileResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    var u = response.body();
                    etName.setText(u.getName());
                    etLastName.setText(u.getLastName());
                    etPhone.setText(u.getPhoneNumber());
                    etAddress.setText(u.getAddress());
                }
            }
            @Override public void onFailure(Call<UserProfileResponseDTO> call, Throwable t) {}
        });
    }

    private void save() {
        UserProfileUpdateRequestDTO dto = new UserProfileUpdateRequestDTO();
        dto.setName(etName.getText().toString());
        dto.setLastName(etLastName.getText().toString());
        dto.setPhoneNumber(etPhone.getText().toString());
        dto.setAddress(etAddress.getText().toString());

        ApiProvider.user().updateProfile(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}