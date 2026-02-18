package com.ftn.mobile.presentation.fragments.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.ChangePasswordRequestDTO;
import com.ftn.mobile.presentation.view.LoginActivity;

public class ChangePasswordFragment extends DialogFragment {

    private EditText etpCurrentPassword, etpNewPassword, etpConfirmNewPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullWidthDialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        etpCurrentPassword = view.findViewById(R.id.etpCurrentPassword);
        etpNewPassword = view.findViewById(R.id.etpNewPassword);
        etpConfirmNewPassword = view.findViewById(R.id.etpConfirmNewPassword);

        view.findViewById(R.id.btnCloseChangePasswordFragment)
                .setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btnConfirmChangePasswordFragment)
                .setOnClickListener(v -> submit());

        return view;
    }

    private void submit() {
        String current = etpCurrentPassword.getText().toString().trim();
        String newPass = etpNewPassword.getText().toString().trim();
        String confirm = etpConfirmNewPassword.getText().toString().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 8) {
            etpNewPassword.setError("Minimum 8 characters");
            return;
        }

        if (!newPass.equals(confirm)) {
            etpConfirmNewPassword.setError("Passwords must match");
            return;
        }

        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO();
        dto.setCurrentPassword(current);
        dto.setNewPassword(newPass);
        dto.setConfirmNewPassword(confirm);

        ApiProvider.user().changePassword(dto).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call,
                                   retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "Password changed. Please login again.",
                            Toast.LENGTH_LONG).show();

                    logoutAndRedirect();
                } else {
                    Toast.makeText(getContext(),
                            "Error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutAndRedirect() {
        //TokenStorage.clear(requireContext());

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}