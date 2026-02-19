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
import com.ftn.mobile.data.remote.dto.enums.UserRole;
import com.ftn.mobile.data.remote.dto.rides.AdminRideHistoryDTO;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideHistoryDTO;
import com.ftn.mobile.data.remote.dto.rides.UserDTO;
import com.ftn.mobile.presentation.adapter.AdminRideHistoryAdapter;
import com.ftn.mobile.presentation.adapter.AllUsersAdapter;
import com.ftn.mobile.presentation.adapter.PassengerRideHistoryAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRideHistoryFragment extends Fragment {
    ArrayList<AdminRideHistoryDTO> rides = new ArrayList<>();
    ArrayList<UserDTO> users = new ArrayList<>();
    AdminRideHistoryAdapter adapter;
    AllUsersAdapter allUsersAdapter;
    private String selectedFromDate = null;
    private String selectedToDate = null;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
    private long userId;
    private int userIndicator;
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
        return inflater.inflate(R.layout.fragment_admin_ride_history, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ride history adapter
        RecyclerView recyclerView = view.findViewById(R.id.adminRideHistoryRecycleView);
        adapter = new AdminRideHistoryAdapter(getContext(), rides, ride -> {
            Long rideId = ride.getRideId();
            if (rideId == null) {
                if (isAdded()) Toast.makeText(requireContext(), "Ride ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }
            openRideDetails(rideId);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //all users adapter
        RecyclerView allUsersRecyclerView = view.findViewById(R.id.allUsersRecycleView);
        allUsersAdapter = new AllUsersAdapter(getContext(), users, user -> {
            userId = user.getId();
            if ("DRIVER".equals(user.getRole())) {
                userIndicator = 1;
            } else {
                userIndicator = 2;
            }
            openUserHistory(userId, userIndicator, null, null);
        });
        allUsersRecyclerView.setAdapter(allUsersAdapter);
        allUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnFilter = view.findViewById(R.id.btnAdminFilterHistory);
        Button btnSortPickup = view.findViewById(R.id.btnAdminSortPickup);
        Button btnSortDestination = view.findViewById(R.id.btnAdminSortDestination);
        Button btnSortStart = view.findViewById(R.id.btnAdminSortStart);
        Button btnSortEnd = view.findViewById(R.id.btnAdminSortEnd);
        Button btnSortCreation = view.findViewById(R.id.btnAdminSortCreation);
        Button btnSortCancellation = view.findViewById(R.id.btnAdminSortCancellation);
        Button btnSortUserWhoCancelled = view.findViewById(R.id.btnAdminSortUserWhoCancelled);
        Button btnSortTotalPrice = view.findViewById(R.id.btnAdminSortTotalPrice);
        Button btnSortPanic = view.findViewById(R.id.btnAdminSortPanic);
        btnFilter.setOnClickListener(v -> showDateRangePicker());
        btnSortPickup.setOnClickListener(v -> applySort("pickupAddress"));
        btnSortDestination.setOnClickListener(v -> applySort("destinationAddress"));
        btnSortStart.setOnClickListener(v -> applySort("startedAt"));
        btnSortEnd.setOnClickListener(v -> applySort("finishedAt"));
        btnSortCreation.setOnClickListener(v -> applySort("createdAt"));
        btnSortCancellation.setOnClickListener(v -> applySort("isCancelled"));
        btnSortUserWhoCancelled.setOnClickListener(v -> applySort("userWhoCancelled"));
        btnSortPanic.setOnClickListener(v -> applySort("isPanic"));
        btnSortTotalPrice.setOnClickListener(v -> applySort("totalPrice"));


        loadAllUsers();

    }
    private void loadAllUsers(){
        ApiProvider.admin().getAllUsers().enqueue(new Callback<List<UserDTO>>() {
            @Override
            public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    users.clear();
                    users.addAll(response.body());
                    allUsersAdapter.notifyDataSetChanged();

                    if (users.isEmpty()) {
                        Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserDTO>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openUserHistory(long userId, int userIndicator, String fromDate, String toDate){

        //prebaci prikaz sa users liste na ride history listu
        RecyclerView usersRv = requireView().findViewById(R.id.allUsersRecycleView);
        RecyclerView ridesRv = requireView().findViewById(R.id.adminRideHistoryRecycleView);

        usersRv.setVisibility(View.GONE);
        ridesRv.setVisibility(View.VISIBLE);
        String fromIso = null;
        String toIso = null;

        if (fromDate != null) fromIso = fromDate + "T00:00:00";
        if (toDate != null) toIso = toDate + "T23:59:59";

        ApiProvider.admin().getAdminRideHistory(userId, userIndicator, sortBy, sortDirection,fromIso, toIso).enqueue(new Callback<List<AdminRideHistoryDTO>>() {
            @Override
            public void onResponse(Call<List<AdminRideHistoryDTO>> call, Response<List<AdminRideHistoryDTO>> response) {
                rides.clear();
                if (response.isSuccessful()) {
                    List<AdminRideHistoryDTO> body = response.body();
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
            public void onFailure(Call<List<AdminRideHistoryDTO>> call, Throwable throwable) {
                rides.clear();
                adapter.notifyDataSetChanged();

                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void applySort(String newSortBy){
        if (userId <= 0) {
            Toast.makeText(requireContext(), "Select a user first", Toast.LENGTH_SHORT).show();
            return;
        }
        openUserHistory(userId, userIndicator, selectedFromDate, selectedToDate);
        if (this.sortBy.equals(newSortBy)) {
            this.sortDirection = this.sortDirection.equals("ASC") ? "DESC" : "ASC";
        } else {
            this.sortBy = newSortBy;
            this.sortDirection = "DESC";
        }
        openUserHistory(userId, userIndicator, selectedFromDate, selectedToDate);
    }
    private void showDateRangePicker() {
        if (userId <= 0) {
            Toast.makeText(requireContext(), "Select a user first", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog fromDatePicker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            selectedFromDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            DatePickerDialog toDatePicker = new DatePickerDialog(requireContext(), (view1, year1, month1, dayOfMonth1) -> {
                selectedToDate = String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth1);
                openUserHistory(userId, userIndicator, selectedFromDate, selectedToDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            toDatePicker.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        fromDatePicker.show();
    }

}