package com.ftn.mobile.presentation.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.ftn.mobile.R;
import com.ftn.mobile.presentation.viewModel.ProfileViewModel;

public class CarInfoDialogFragment extends DialogFragment {
    private ProfileViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.FullWidthDialog);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_car_info, container, false);

        viewModel = new ViewModelProvider(requireActivity())
                .get(ProfileViewModel.class);

        TextView tvModel = view.findViewById(R.id.tvCarModelValue);
        TextView tvType = view.findViewById(R.id.tvCarTypeValue);
        TextView tvPlates = view.findViewById(R.id.tvCarPlatesValue);
        TextView tvSeats = view.findViewById(R.id.tvCarSeatsValue);
        TextView tvPet = view.findViewById(R.id.tvPetFriendlyValue);
        TextView tvBaby = view.findViewById(R.id.tvBabyFriendlyValue);

        viewModel.getCar().observe(getViewLifecycleOwner(), car -> {
            tvModel.setText(car.getModel());
            tvType.setText(car.getType());
            tvPlates.setText(car.getPlates());
            tvSeats.setText(car.getSeats());

            setYesNo(tvPet, car.isPetFriendly());
            setYesNo(tvBaby, car.isBabyFriendly());
        });

        viewModel.loadDriverVehicleInfo();

        view.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dismiss());

        return view;
    }

    private void setYesNo(TextView tv, boolean value) {
        if (value) {
            tv.setText("Yes");
            tv.setTextColor(requireContext().getColor(R.color.accent));
        } else {
            tv.setText("No");
            tv.setTextColor(requireContext().getColor(R.color.error));
        }
    }
}
