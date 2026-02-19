package com.ftn.mobile.presentation.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.DriverRideHistoryDTO;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideDetailsResponseDTO;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideHistoryDTO;
import com.ftn.mobile.presentation.adapter.DriverHistoryAdapter;
import com.ftn.mobile.presentation.adapter.PassengerRideHistoryAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideHistoryFragment extends Fragment {
    ArrayList<PassengerRideHistoryDTO> rides = new ArrayList<>();
    PassengerRideHistoryAdapter adapter;
    private String selectedFromDate = null;
    private String selectedToDate = null;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
    private void openRideDetails(long rideId) {
        PassengerRideDetailsFragment fragment = PassengerRideDetailsFragment.newInstance(rideId);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_history, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.passengerRideHistoryRecycleView);
        adapter = new PassengerRideHistoryAdapter(getContext(), rides, ride -> {
            Long rideId = ride.getRideId();
            if (rideId == null) {
                if (isAdded()) Toast.makeText(requireContext(), "Ride ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }
            openRideDetails(rideId);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnFilter = view.findViewById(R.id.btnFilterHistory);
        Button btnSortPickup = view.findViewById(R.id.btnSortPickup);
        Button btnSortDestination = view.findViewById(R.id.btnSortDestination);
        Button btnSortStart = view.findViewById(R.id.btnSortStart);
        Button btnSortEnd = view.findViewById(R.id.btnSortEnd);
        Button btnSortCreation = view.findViewById(R.id.btnSortCreation);
        btnFilter.setOnClickListener(v -> showDateRangePicker());
        btnSortPickup.setOnClickListener(v -> applySort("pickupAddress"));
        btnSortDestination.setOnClickListener(v -> applySort("destinationAddress"));
        btnSortStart.setOnClickListener(v -> applySort("startedAt"));
        btnSortEnd.setOnClickListener(v -> applySort("finishedAt"));
        btnSortCreation.setOnClickListener(v -> applySort("createdAt"));
        loadPassengerRideHistory(null, null);
    }
    private void applySort(String newSortBy){
        if (this.sortBy.equals(newSortBy)) {
            this.sortDirection = this.sortDirection.equals("ASC") ? "DESC" : "ASC";
        } else {
            this.sortBy = newSortBy;
            this.sortDirection = "DESC";
        }
        loadPassengerRideHistory(selectedFromDate, selectedToDate);
    }
    private void showDateRangePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog fromDatePicker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedFromDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            DatePickerDialog toDatePicker = new DatePickerDialog(requireContext(), (view1, year1, month1, dayOfMonth1) -> {
                selectedToDate = String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth1);
                loadPassengerRideHistory(selectedFromDate, selectedToDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            toDatePicker.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        fromDatePicker.show();
    }
    private void loadPassengerRideHistory(String fromDate, String toDate){
        String fromIso = null;
        String toIso = null;

        if (fromDate != null) fromIso = fromDate + "T00:00:00";
        if (toDate != null) toIso = toDate + "T23:59:59";
        ApiProvider.user().getPassengerRideHistory(sortBy,sortDirection,fromIso,toIso).enqueue(new Callback<List<PassengerRideHistoryDTO>>() {
            @Override
            public void onResponse(Call<List<PassengerRideHistoryDTO>> call, Response<List<PassengerRideHistoryDTO>> response) {
                rides.clear();
                if (response.isSuccessful()) {
                    List<PassengerRideHistoryDTO> body = response.body();
                    if (body != null && !body.isEmpty()) {
                        rides.addAll(body);
                    } else {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "No rides found", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<PassengerRideHistoryDTO>> call, Throwable throwable) {
                rides.clear();
                adapter.notifyDataSetChanged();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}