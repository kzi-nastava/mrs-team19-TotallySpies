package com.ftn.mobile.presentation.viewModel;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ftn.mobile.data.model.VehiclePricing;
import com.ftn.mobile.data.model.VehicleType;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.UpdatePriceDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricingViewModel extends ViewModel {

    private final MutableLiveData<List<VehiclePricing>> prices = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    public LiveData<List<VehiclePricing>> getPrices() { return prices; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public void fetchPrices() {
        ApiProvider.price().getCurrentPrices().enqueue(new Callback<List<VehiclePricing>>() {
            @Override
            public void onResponse(Call<List<VehiclePricing>> call, Response<List<VehiclePricing>> response) {
                if (response.isSuccessful()) {
                    prices.setValue(response.body());
                } else {
                    statusMessage.setValue("error while getting prices.");
                }
            }

            @Override
            public void onFailure(Call<List<VehiclePricing>> call, Throwable t) {
                statusMessage.setValue("no server connection.");
            }
        });
    }

    public void updateAllPrices(double std, double lux, double van) {
        updateSinglePrice(VehicleType.STANDARD, std);
        updateSinglePrice(VehicleType.LUXURY, lux);
        updateSinglePrice(VehicleType.VAN, van);
    }

    private void updateSinglePrice(VehicleType type, double price) {
        ApiProvider.price().updatePrice(new UpdatePriceDTO(type, price)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    statusMessage.setValue("Price for " + type + " updated.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                statusMessage.setValue("Unsuccessful price update " + type);
            }
        });
    }
}