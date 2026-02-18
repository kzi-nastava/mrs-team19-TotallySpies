package com.ftn.mobile.presentation.viewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.VehicleDisplayInfoDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<VehicleDisplayInfoDTO>> vehicles = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    public LiveData<String> getError() { return error; }
    public LiveData<List<VehicleDisplayInfoDTO>> getVehicles() {
        return vehicles;
    }

    public void fetchInitialVehicles() {
        ApiProvider.vehicle().getAllVehicles().enqueue(new Callback<List<VehicleDisplayInfoDTO>>() {
            @Override
            public void onResponse(Call<List<VehicleDisplayInfoDTO>> call, Response<List<VehicleDisplayInfoDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    vehicles.postValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<VehicleDisplayInfoDTO>> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }
    public void updateVehicleData(List<VehicleDisplayInfoDTO> newVehicles) {
        vehicles.postValue(newVehicles);
    }
}