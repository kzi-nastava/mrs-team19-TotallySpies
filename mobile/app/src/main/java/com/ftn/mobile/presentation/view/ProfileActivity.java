package com.ftn.mobile.presentation.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ftn.mobile.R;
import com.ftn.mobile.data.local.UserRoleManger;
import com.ftn.mobile.presentation.fragments.CarInfoDialogFragment;
import com.ftn.mobile.presentation.fragments.ChangePasswordFragment;
import com.ftn.mobile.presentation.viewModel.ProfileViewModel;
import com.ftn.mobile.utils.ProfileImageManager;
import com.google.android.material.imageview.ShapeableImageView;


public class ProfileActivity extends AppCompatActivity {
    private ShapeableImageView imgUser;
    private ImageButton updateImageButton;
    private ProfileViewModel viewModel;

    private ProfileImageManager profileImageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

            profileImageManager.loadImage(user.getProfileImageUrl());

        });

        // OBSERVE DRIVER ACTIVITY
        viewModel.getDriverActivity().observe(this, activity -> {
            if (activity != null) {
                tvActiveHours.setText(activity);
            }
        });

        // OBSERVE ROLE
        UserRoleManger.getRoleLiveData().observe(this, role -> {
            if (role == null) return;
            updateUIForRole(role);
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
        profileImageManager = new ProfileImageManager(
                this,
                imgUser,
                () -> viewModel.loadProfile()
        );

        updateImageButton.setOnClickListener(v ->
                profileImageManager.checkPermissionAndPick()
        );

        // BACK
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void showCarInfoDialog() {
        new CarInfoDialogFragment().show(getSupportFragmentManager(), "carInfo");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileImageManager.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ProfileImageManager.REQUEST_STORAGE_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            profileImageManager.checkPermissionAndPick();
        }
    }

    private void updateUIForRole(String role) {
        View adminPanel = findViewById(R.id.ConstraintLayoutAdminInfo);
        View driverPanel = findViewById(R.id.ConstraintLayoutDriver);

        switch (role) {
            case "ROLE_ADMIN":
                if (adminPanel != null) adminPanel.setVisibility(View.VISIBLE);
                if (driverPanel != null) driverPanel.setVisibility(View.GONE);
                break;
            case "ROLE_DRIVER":
                if (adminPanel != null) adminPanel.setVisibility(View.GONE);
                if (driverPanel != null) driverPanel.setVisibility(View.VISIBLE);
                break;
            default: // ROLE_PASSENGER
                if (adminPanel != null) adminPanel.setVisibility(View.GONE);
                if (driverPanel != null) driverPanel.setVisibility(View.GONE);
                break;
        }
    }

}