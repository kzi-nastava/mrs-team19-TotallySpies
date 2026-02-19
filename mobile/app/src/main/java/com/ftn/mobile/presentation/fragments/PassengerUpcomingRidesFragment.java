package com.ftn.mobile.presentation.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.PassengerUpcomingRideDTO;
import com.ftn.mobile.presentation.adapter.PassengerUpcomingRidesAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerUpcomingRidesFragment extends Fragment {
    private RecyclerView recyclerView;
    private PassengerUpcomingRidesAdapter adapter;
    private List<PassengerUpcomingRideDTO> ridesList = new ArrayList<>();
    private ProgressBar loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_upcoming_rides, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvUpcomingRides);
        loader = view.findViewById(R.id.loader);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PassengerUpcomingRidesAdapter(getContext(), ridesList);
        recyclerView.setAdapter(adapter);

        loadUpcomingRides();
    }

    private void loadUpcomingRides() {
        loader.setVisibility(View.VISIBLE);

        ApiProvider.ride().getPassengerUpcoming().enqueue(new Callback<List<PassengerUpcomingRideDTO>>() {
            @Override
            public void onResponse(Call<List<PassengerUpcomingRideDTO>> call, Response<List<PassengerUpcomingRideDTO>> response) {
                loader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ridesList.clear();
                    ridesList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (ridesList.isEmpty()) {
                        Toast.makeText(getContext(), "You have no upcoming rides", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load rides", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PassengerUpcomingRideDTO>> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
