package com.ftn.mobile.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.ftn.mobile.BuildConfig;
import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.presentation.fragments.CarInfoDialogFragment;
import com.ftn.mobile.presentation.fragments.ChangePasswordFragment;
import com.ftn.mobile.presentation.viewModel.ProfileViewModel;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int REQUEST_STORAGE_PERMISSION = 102;

    private static final String MOCK_JWT = "MOCK_JWT";
    private static final String MOCK_ROLE = "ADMIN"; // ili ADMIN za test

    private ShapeableImageView imgUser;
    private ImageButton updateImageButton;
    private ProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // ⬅️ MOCK JWT + ROLE u SharedPreferences
        mockLogin();

        // ViewModel
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Views
        imgUser = findViewById(R.id.imgUser);
        updateImageButton = findViewById(R.id.updateImageButton);

        TextView tvNameLastName = findViewById(R.id.tvNameLastName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvActiveHours = findViewById(R.id.tvActiveHours);

        // OBSERVE USER
        viewModel.getUser().observe(this, user -> {
            if (user == null) return;

            tvNameLastName.setText(user.getName() + " " + user.getLastName());
            tvEmail.setText(user.getEmail());
            tvPhoneNumber.setText(user.getPhoneNumber());
            tvAddress.setText(user.getAddress());

            String profilePictureUrl;
            if (user.getProfileImageUrl() == null ||
                    user.getProfileImageUrl().equals("null") ||
                    user.getProfileImageUrl().isEmpty()) {

                profilePictureUrl = BuildConfig.BASE_URL +
                        "api/v1/users/image/default-profile-image.jpg";
            } else {
                String filename = user.getProfileImageUrl()
                        .substring(user.getProfileImageUrl().lastIndexOf("/") + 1);

                profilePictureUrl = BuildConfig.BASE_URL +
                        "api/v1/users/image/" + filename;
            }

            Glide.with(ProfileActivity.this)
                    .load(profilePictureUrl)
                    .error(R.mipmap.page_mock)
                    .into(imgUser);

            updateUIForRole();
        });

        // OBSERVE DRIVER ACTIVITY
        viewModel.getDriverActivity().observe(this, activity -> {
            if (activity != null) {
                tvActiveHours.setText(activity);
            }
        });

        // LOAD DATA FROM SERVER
        viewModel.loadProfile();
        viewModel.loadDriverActivity();

        // ADMIN BUTTONS
        if (findViewById(R.id.btnManageUsers) != null) {
            findViewById(R.id.btnManageUsers).setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminListActivity.class);
                intent.putExtra("TYPE", "USERS");
                startActivity(intent);
            });
        }

        if (findViewById(R.id.btnManageDrivers) != null) {
            findViewById(R.id.btnManageDrivers).setOnClickListener(v -> {
                Intent intent = new Intent(this, AdminListActivity.class);
                intent.putExtra("TYPE", "DRIVERS");
                startActivity(intent);
            });
        }

        // DRIVER CAR INFO
        if (findViewById(R.id.btnCarInfo) != null) {
            findViewById(R.id.btnCarInfo).setOnClickListener(v -> showCarInfoDialog());
        }

        // CHANGE PASSWORD
        if (findViewById(R.id.btnChangePassword) != null) {
            findViewById(R.id.btnChangePassword).setOnClickListener(v ->
                    new ChangePasswordFragment()
                            .show(getSupportFragmentManager(), "changePassword"));
        }

        // EDIT PROFILE
        if (findViewById(R.id.btnEdit) != null) {
            findViewById(R.id.btnEdit).setOnClickListener(v ->
                    startActivity(new Intent(this, EditProfileActivity.class)));
        }

        // CHANGE IMAGE
        updateImageButton.setOnClickListener(v -> checkStoragePermissionAndPickImage());

        // BACK
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void showCarInfoDialog() {
        new CarInfoDialogFragment().show(getSupportFragmentManager(), "carInfo");
    }

    private void checkStoragePermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ koristi READ_MEDIA_IMAGES
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION);
            }
        } else {
            // Android ≤ 12 koristi READ_EXTERNAL_STORAGE
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            uploadImage(selectedImageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        try {
            // Pretvori u MultipartBody.Part
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();

            RequestBody requestFile = RequestBody.create(bytes, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "profile.jpg", requestFile);

            // Pozovi API
            ApiProvider.user().uploadImage(body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile image updated!", Toast.LENGTH_SHORT).show();
                        viewModel.loadProfile(); // refreshuj prikaz
                    } else {
                        Toast.makeText(ProfileActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ProfileActivity.this, "Upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Permission denied. Cannot change profile image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mockLogin() {
        SharedPreferences sp = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("jwt_token", MOCK_JWT);
        editor.putString("role", MOCK_ROLE);
        editor.apply();
    }

    private void updateUIForRole() {
        SharedPreferences sp = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String role = sp.getString("role", "USER"); // default je USER

        View adminPanel = findViewById(R.id.ConstraintLayoutAdminInfo);
        View driverPanel = findViewById(R.id.ConstraintLayoutDriver);

        if ("DRIVER".equals(role)) {
            // Sakrij cijeli admin panel
            if (adminPanel != null) adminPanel.setVisibility(View.GONE);
        } else if ("ADMIN".equals(role)) {
            // Sakrij druge stvari koje nisu admin
            if (driverPanel != null) driverPanel.setVisibility(View.GONE);
        }
    }

}