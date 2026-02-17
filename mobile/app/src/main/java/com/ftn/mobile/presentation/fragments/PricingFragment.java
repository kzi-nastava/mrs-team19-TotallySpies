package com.ftn.mobile.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.ftn.mobile.R;
import com.ftn.mobile.presentation.viewModel.PricingViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.ftn.mobile.data.model.VehicleType;

public class PricingFragment extends Fragment {

    private PricingViewModel viewModel;
    private TextInputEditText editStandard, editLuxury, editVan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pricing, container, false);

        viewModel = new ViewModelProvider(this).get(PricingViewModel.class);

        editStandard = view.findViewById(R.id.editStandardPrice);
        editLuxury = view.findViewById(R.id.editLuxuryPrice);
        editVan = view.findViewById(R.id.editVanPrice);

        // load prices
        viewModel.getPrices().observe(getViewLifecycleOwner(), vehiclePricings -> {
            for (var p : vehiclePricings) {
                switch (p.getVehicleType()) {
                    case STANDARD: editStandard.setText(String.valueOf(p.getBasePrice())); break;
                    case LUXURY: editLuxury.setText(String.valueOf(p.getBasePrice())); break;
                    case VAN: editVan.setText(String.valueOf(p.getBasePrice())); break;
                }
            }
        });

        // toast updates
        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
        viewModel.fetchPrices();

        // save action
        view.findViewById(R.id.btnSavePrices).setOnClickListener(v -> saveChanges());

        return view;
    }

    private void saveChanges() {
        try {
            double std = Double.parseDouble(editStandard.getText().toString());
            double lux = Double.parseDouble(editLuxury.getText().toString());
            double van = Double.parseDouble(editVan.getText().toString());

            viewModel.updateAllPrices(std, lux, van);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "enter valid nums!", Toast.LENGTH_SHORT).show();
        }
    }
}