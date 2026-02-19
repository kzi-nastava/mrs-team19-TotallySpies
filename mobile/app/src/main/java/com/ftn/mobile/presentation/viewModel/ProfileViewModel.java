package com.ftn.mobile.presentation.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ftn.mobile.data.model.CarInfo;
import com.ftn.mobile.data.model.User;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.DriverActivityResponseDTO;
import com.ftn.mobile.data.remote.dto.DriverBlockedStatusDTO;
import com.ftn.mobile.data.remote.dto.UserProfileResponseDTO;
import com.ftn.mobile.data.remote.dto.VehicleInfoResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<DriverBlockedStatusDTO> blockedStatus = new MutableLiveData<>();

    public LiveData<DriverBlockedStatusDTO> getBlockedStatus() {
        return blockedStatus;
    }
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<CarInfo> carLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> driverActivity = new MutableLiveData<>();

    public LiveData<User> getUser() { return userLiveData; }
    public LiveData<String> getError() { return error; }
    public LiveData<CarInfo> getCar() { return carLiveData; }
    public LiveData<String> getDriverActivity() { return driverActivity; }

    public void loadProfile() {
        ApiProvider.user().getProfile().enqueue(new Callback<UserProfileResponseDTO>() {
            @Override
            public void onResponse(Call<UserProfileResponseDTO> call,
                                   Response<UserProfileResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponseDTO dto = response.body();

                    User u = new User(dto.getEmail(), dto.getName(), dto.getLastName(), dto.getAddress(), dto.getProfilePicture(), dto.getPhoneNumber());
                    userLiveData.postValue(u);
                } else {
                    error.postValue("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponseDTO> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    public void loadDriverVehicleInfo() {
        ApiProvider.driver().getVehicleInfo().enqueue(new Callback<VehicleInfoResponseDTO>() {
            @Override
            public void onResponse(Call<VehicleInfoResponseDTO> call,
                                   Response<VehicleInfoResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VehicleInfoResponseDTO dto = response.body();

                    CarInfo car = new CarInfo(
                            dto.getModel(),
                            dto.getVehicleType().name(),
                            dto.getLicensePlate(),
                            String.valueOf(dto.getPassengerCapacity()),
                            dto.isPetTransport(),
                            dto.isBabyTransport()
                    );

                    carLiveData.postValue(car);
                } else {
                    error.postValue("Driver info error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<VehicleInfoResponseDTO> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    public void loadDriverActivity() {
        ApiProvider.driver().getActivity().enqueue(new Callback<DriverActivityResponseDTO>() {
            @Override
            public void onResponse(Call<DriverActivityResponseDTO> call, Response<DriverActivityResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    long minutes = response.body().getMinutesLast24h();
                    String formatted = formatMinutes(minutes);
                    driverActivity.postValue(formatted);
                } else {
                    error.postValue("Driver activity error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DriverActivityResponseDTO> call, Throwable t) {
                error.postValue(t.getMessage());
            }
        });
    }

    private String formatMinutes(long totalMinutes) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return hours + "h " + minutes + "m";
    }

    public void loadBlockedStatus() {
        ApiProvider.driver().getBlockedStatus().enqueue(new Callback<DriverBlockedStatusDTO>() {
            @Override
            public void onResponse(Call<DriverBlockedStatusDTO> call, Response<DriverBlockedStatusDTO> response) {
                if (response.isSuccessful()) {
                    blockedStatus.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<DriverBlockedStatusDTO> call, Throwable t) { /* handle error */ }
        });
    }

}
