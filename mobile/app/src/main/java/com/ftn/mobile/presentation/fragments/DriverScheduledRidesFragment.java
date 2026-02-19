package com.ftn.mobile.presentation.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.ScheduledRideDTO;
import com.ftn.mobile.data.remote.dto.rides.CancelRideDTO;
import com.ftn.mobile.presentation.adapter.ScheduledRidesAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverScheduledRidesFragment extends Fragment implements ScheduledRidesAdapter.OnRideActionListener {

    private ArrayList<ScheduledRideDTO> activeRides = new ArrayList<>();
    private ScheduledRidesAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_scheduled_rides, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.adhRecycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ScheduledRidesAdapter(getContext(), activeRides, this);
        recyclerView.setAdapter(adapter);

        loadActiveAndScheduledRides();
    }

    private void loadActiveAndScheduledRides() {
        ApiProvider.ride().getScheduledRides().enqueue(new Callback<List<ScheduledRideDTO>>() {
            @Override
            public void onResponse(Call<List<ScheduledRideDTO>> call, Response<List<ScheduledRideDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeRides.clear();
                    activeRides.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (activeRides.isEmpty()) {
                        Toast.makeText(getContext(), "No rides found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ScheduledRideDTO>> call, Throwable t) {
                Log.e("API_ERROR", "error while loading: " + t.getMessage());
            }
        });
    }

    @Override
    public void onFinishRide(Long rideId) {
        ApiProvider.ride().finishRide(rideId).enqueue(new Callback<ScheduledRideDTO>() {
            @Override
            public void onResponse(Call<ScheduledRideDTO> call, Response<ScheduledRideDTO> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "ride finished!", Toast.LENGTH_LONG).show();
                    loadActiveAndScheduledRides();
                } else {
                    Toast.makeText(getContext(), "error while finishing ride", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ScheduledRideDTO> call, Throwable t) {
                Toast.makeText(getContext(), "server error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStartRide(Long rideId) {
        ApiProvider.ride().startRide(rideId).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ride successfully started!", Toast.LENGTH_SHORT).show();
                    loadActiveAndScheduledRides();
                } else {
                    Toast.makeText(getContext(), "Failed to start ride.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Log.e("API_ERROR", "Error starting ride: " + t.getMessage());
                Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelRide(Long rideId) {

        final EditText input = new EditText(getContext());
        input.setHint("Enter cancellation reason");
        input.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Ride")
                .setMessage("Please enter reason for cancellation:")
                .setView(input)
                .setPositiveButton("Cancel Ride", (dialog, which) -> {

                    String reason = input.getText().toString().trim();

                    if (reason.isEmpty()) {
                        Toast.makeText(getContext(), "Reason is required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    String isoTime = LocalDateTime.now().format(formatter);

                    CancelRideDTO request = new CancelRideDTO(
                            rideId,
                            reason,
                            isoTime
                    );

                    ApiProvider.ride().cancelRide(request)
                            .enqueue(new Callback<String>() {

                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                        loadActiveAndScheduledRides();
                                    } else {
                                        Toast.makeText(getContext(), "Failed to cancel ride", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Log.e("API_ERROR", "Cancel error: " + t.getMessage());
                                    //Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show();
                                }

                            });

                })
                .setNegativeButton("Back", (dialog, which) -> dialog.dismiss())
                .show();
    }
    @Override
    public void onTrackRide(Long rideId) {
        RideTrackingFragment fragment = new RideTrackingFragment();

        Bundle args = new Bundle();
        args.putLong("rideId", rideId);
        fragment.setArguments(args);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}