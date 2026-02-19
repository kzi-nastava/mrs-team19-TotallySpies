package com.ftn.mobile.presentation.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.ActiveRideDTO;
import com.ftn.mobile.data.remote.dto.VehicleDisplayInfoDTO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTrackingViewModel extends ViewModel {
    private final MutableLiveData<List<VehicleDisplayInfoDTO>> vehicles = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<ActiveRideDTO>> activeRides = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<VehicleDisplayInfoDTO>> getVehicles() { return vehicles; }
    public LiveData<List<ActiveRideDTO>> getActiveRides() { return activeRides; }

    public void fetchInitialData() {
        ApiProvider.vehicle().getAllVehicles().enqueue(new Callback<List<VehicleDisplayInfoDTO>>() {
            @Override
            public void onResponse(Call<List<VehicleDisplayInfoDTO>> call, Response<List<VehicleDisplayInfoDTO>> response) {
                if (response.isSuccessful()) vehicles.postValue(response.body());
            }
            @Override
            public void onFailure(Call<List<VehicleDisplayInfoDTO>> call, Throwable t) { error.postValue(t.getMessage()); }
        });
        ApiProvider.ride().getActiveRidesForAdmin().enqueue(new Callback<List<ActiveRideDTO>>() {
            @Override
            public void onResponse(Call<List<ActiveRideDTO>> call, Response<List<ActiveRideDTO>> response) {
                if (response.isSuccessful()) activeRides.postValue(response.body());
            }
            @Override
            public void onFailure(Call<List<ActiveRideDTO>> call, Throwable t) { error.postValue(t.getMessage()); }
        });
    }

    public void updateVehicleData(List<VehicleDisplayInfoDTO> updates) {
        vehicles.postValue(updates);
    }
}