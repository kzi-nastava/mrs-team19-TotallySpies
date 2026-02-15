package com.ftn.mobile.presentation.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.local.UserRoleManger;
import com.ftn.mobile.presentation.view.AdminListActivity;
import com.ftn.mobile.presentation.view.AdminProfileChangeRequestsActivity;
import com.ftn.mobile.presentation.view.EditProfileActivity;
import com.ftn.mobile.presentation.viewModel.ProfileViewModel;
import com.ftn.mobile.utils.ProfileImageManager;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileFragment extends Fragment {

    private ShapeableImageView imgUser;
    private ImageButton updateImageButton;
    private ProfileViewModel viewModel;
    private ProfileImageManager profileImageManager;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK) {
                            viewModel.loadProfile();
                        }
                    });

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        imgUser = view.findViewById(R.id.imgUser);
        updateImageButton = view.findViewById(R.id.updateImageButton);

        TextView tvNameLastName = view.findViewById(R.id.tvNameLastName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        TextView tvAddress = view.findViewById(R.id.tvAddress);
        TextView tvActiveHours = view.findViewById(R.id.tvActiveHours);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;
            tvNameLastName.setText(user.getName() + " " + user.getLastName());
            tvEmail.setText(user.getEmail());
            tvPhoneNumber.setText(user.getPhoneNumber());
            tvAddress.setText(user.getAddress());
            profileImageManager.loadImage(user.getProfileImageUrl());
        });

        viewModel.getDriverActivity().observe(getViewLifecycleOwner(), activity -> {
            if (activity != null) tvActiveHours.setText(activity);
        });

        UserRoleManger.getRoleLiveData().observe(getViewLifecycleOwner(), role -> {
            if (role != null) updateUIForRole(view, role);
        });

        viewModel.loadProfile();
        viewModel.loadDriverActivity();

        view.findViewById(R.id.btnManageUsers)
                .setOnClickListener(v -> startActivity(
                        new Intent(getContext(), AdminListActivity.class)
                                .putExtra("TYPE", "USERS")));

        view.findViewById(R.id.btnManageDrivers)
                .setOnClickListener(v -> startActivity(
                        new Intent(getContext(), AdminListActivity.class)
                                .putExtra("TYPE", "DRIVERS")));

        view.findViewById(R.id.btnChangeRequests)
                .setOnClickListener(v ->
                        startActivity(new Intent(getContext(), AdminProfileChangeRequestsActivity.class)));

        view.findViewById(R.id.btnCarInfo)
                .setOnClickListener(v ->
                        new CarInfoDialogFragment().show(getParentFragmentManager(), "carInfo"));

        view.findViewById(R.id.btnChangePassword)
                .setOnClickListener(v ->
                        new ChangePasswordFragment().show(getParentFragmentManager(), "changePassword"));

        view.findViewById(R.id.btnEdit)
                .setOnClickListener(v ->
                        editProfileLauncher.launch(new Intent(getContext(), EditProfileActivity.class)));

        profileImageManager = new ProfileImageManager(
                requireActivity(), imgUser, () -> viewModel.loadProfile());

        updateImageButton.setOnClickListener(v ->
                profileImageManager.checkPermissionAndPick());

        return view;
    }

    private void updateUIForRole(View root, String role) {
        View adminPanel = root.findViewById(R.id.ConstraintLayoutAdminInfo);
        View driverPanel = root.findViewById(R.id.ConstraintLayoutDriver);

        if ("ROLE_ADMIN".equals(role)) {
            adminPanel.setVisibility(View.VISIBLE);
            driverPanel.setVisibility(View.GONE);
        } else if ("ROLE_DRIVER".equals(role)) {
            adminPanel.setVisibility(View.GONE);
            driverPanel.setVisibility(View.VISIBLE);
        } else {
            adminPanel.setVisibility(View.GONE);
            driverPanel.setVisibility(View.GONE);
        }
    }
}