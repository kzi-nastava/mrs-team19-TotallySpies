package com.ftn.mobile.presentation.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.CreateDriverRequestDTO;
import com.ftn.mobile.utils.DialogBox;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRegistrationFragment extends Fragment {


    public DriverRegistrationFragment() {
        // Required empty public constructor
    }
    private EditText etEmail, etName, etLastName, etAddress, etPhone, etModel, etPlate, etSeats;
    private AutoCompleteTextView vehicleTypeInput;
    private CheckBox cbBaby, cbPet;
    private Button btnRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_registration, container, false);

        initViews(view);
        setupVehicleTypeSpinner();

        btnRegister.setOnClickListener(v -> {
            if (validateFields()) {
                sendDataToServer();
            }
        });

        return view;
    }

    private void initViews(View v) {
        etEmail = v.findViewById(R.id.etEmailReg);
        etName = v.findViewById(R.id.etNameReg);
        etLastName = v.findViewById(R.id.etLastNameReg);
        etAddress = v.findViewById(R.id.etAddressReg);
        etPhone = v.findViewById(R.id.etPhoneNumberReg);
        etModel = v.findViewById(R.id.etVehicleModelReg);
        etPlate = v.findViewById(R.id.etLicencePlateReg);
        etSeats = v.findViewById(R.id.etSeatsNumber);
        vehicleTypeInput = v.findViewById(R.id.vehicleTypeInput);
        cbBaby = v.findViewById(R.id.cbBabyFriendly);
        cbPet = v.findViewById(R.id.cbPetFriendly);
        btnRegister = v.findViewById(R.id.btnRegisterDriver);
    }

    private void setupVehicleTypeSpinner() {
        String[] vehicleTypes = getResources().getStringArray(R.array.vehicle_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, vehicleTypes);
        vehicleTypeInput.setAdapter(adapter);

        vehicleTypeInput.setOnClickListener(v -> vehicleTypeInput.showDropDown());
    }

    private boolean validateFields() {
        boolean isValid = true;

        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            isValid = false;
        }

        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("First name is required");
            isValid = false;
        }
        if (etLastName.getText().toString().trim().isEmpty()) {
            etLastName.setError("Last name is required");
            isValid = false;
        }

        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            isValid = false;
        } else if (phone.length() < 11) {
            etPhone.setError("Phone number is too short");
            isValid = false;
        }

        if (etModel.getText().toString().trim().isEmpty()) {
            etModel.setError("Vehicle model is required");
            isValid = false;
        }
        if (etPlate.getText().toString().trim().isEmpty()) {
            etPlate.setError("License plate is required");
            isValid = false;
        }

        String seats = etSeats.getText().toString().trim();
        if (seats.isEmpty()) {
            etSeats.setError("Number of seats is required");
            isValid = false;
        } else {
            try {
                int s = Integer.parseInt(seats);
                if (s < 1 || s > 8) {
                    etSeats.setError("Seats must be between 1 and 8");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etSeats.setError("Invalid number");
                isValid = false;
            }
        }

        if (vehicleTypeInput.getText().toString().isEmpty()) {
            vehicleTypeInput.setError("Please select a vehicle type");
            isValid = false;
        }

        return isValid;
    }

    private void sendDataToServer() {
        String email = etEmail.getText().toString().trim();
        String firstName = etName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String licensePlate = etPlate.getText().toString().trim();
        int seats = Integer.parseInt(etSeats.getText().toString().trim());

        String vehicleType = vehicleTypeInput.getText().toString().toUpperCase();

        boolean babyFriendly = cbBaby.isChecked();
        boolean petFriendly = cbPet.isChecked();

        CreateDriverRequestDTO dto = new CreateDriverRequestDTO(
                email, firstName, lastName, phone, address,
                model, vehicleType, licensePlate, seats,
                babyFriendly, petFriendly
        );

        ApiProvider.admin().createDriver(dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    DialogBox.showDialog(getActivity(), "Success",
                            "Driver created successfully! An activation email has been sent.");
                    clearForm();
                } else {
                    String errorMessage = "Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DialogBox.showDialog(getActivity(), "Registration Failed", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                DialogBox.showDialog(getActivity(), "Network Error", t.getMessage());
            }
        });
    }

    private void clearForm() {
        etEmail.setText("");
        etName.setText("");
        etLastName.setText("");
        etPhone.setText("");
        etAddress.setText("");
        etModel.setText("");
        etPlate.setText("");
        etSeats.setText("");
        vehicleTypeInput.setText("");
        cbBaby.setChecked(false);
        cbPet.setChecked(false);
    }
}