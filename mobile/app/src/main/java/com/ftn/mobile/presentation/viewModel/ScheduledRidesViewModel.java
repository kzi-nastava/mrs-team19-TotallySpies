package com.ftn.mobile.presentation.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.ScheduledRideDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduledRidesViewModel extends ViewModel {
    private MutableLiveData<List<ScheduledRideDTO>> rides = new MutableLiveData<>();

    public void loadRides() {
        ApiProvider.ride().getScheduledRides().enqueue(new Callback<List<ScheduledRideDTO>>() {
            @Override
            public void onResponse(Call<List<ScheduledRideDTO>> call, Response<List<ScheduledRideDTO>> response) {
                if(response.isSuccessful()) rides.setValue(response.body());
            }
            @Override public void onFailure(Call<List<ScheduledRideDTO>> call, Throwable t) {}
        });
    }

    public void finishRide(Long id) {
        ApiProvider.ride().finishRide(id).enqueue(new Callback<ScheduledRideDTO>() {
            @Override
            public void onResponse(Call<ScheduledRideDTO> call, Response<ScheduledRideDTO> response) {
                loadRides();
            }
            @Override public void onFailure(Call<ScheduledRideDTO> call, Throwable t) {}
        });
    }
}