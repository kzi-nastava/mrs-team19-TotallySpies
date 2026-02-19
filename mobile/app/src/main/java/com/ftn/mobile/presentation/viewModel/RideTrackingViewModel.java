package com.ftn.mobile.presentation.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.InconsistencyReportRequestDTO;
import com.ftn.mobile.data.remote.dto.ReviewRequestDTO;
import com.ftn.mobile.data.remote.dto.ReviewResponseDTO;
import com.ftn.mobile.data.remote.dto.RideTrackingDTO;
import com.google.gson.Gson;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class RideTrackingViewModel extends ViewModel {
    private final MutableLiveData<RideTrackingDTO> rideData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Gson gson = new Gson();
    private final MutableLiveData<Boolean> driverRated = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> vehicleRated = new MutableLiveData<>(false);

    public LiveData<Boolean> getDriverRated() { return driverRated; }
    public LiveData<Boolean> getVehicleRated() { return vehicleRated; }
    public LiveData<RideTrackingDTO> getRideData() { return rideData; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void initStomp(Long rideId) {
        if (mStompClient != null) return; // already connected

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/ws-transport/websocket");

        compositeDisposable.add(mStompClient.topic("/topic/ride/" + rideId).subscribe(msg -> {
            Log.d("STOMP_PAYLOAD", msg.getPayload());
            RideTrackingDTO dto = gson.fromJson(msg.getPayload(), RideTrackingDTO.class);
            rideData.postValue(dto);
        }, throwable -> Log.e("STOMP", "Error", throwable)));

        mStompClient.connect();
    }

    public void sendReport(Long rideId, String reason) {
        InconsistencyReportRequestDTO dto = new InconsistencyReportRequestDTO(rideId, reason);
        ApiProvider.ride().reportInconsistency(rideId, dto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) toastMessage.postValue("Report sent!");
                else toastMessage.postValue("error while sending");
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                toastMessage.postValue("server error.");
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mStompClient != null) mStompClient.disconnect();
        compositeDisposable.clear();
    }

    public void loadRideById(long rideId) {
        ApiProvider.ride().getRideLocation(rideId).enqueue(new Callback<RideTrackingDTO>() {
            @Override
            public void onResponse(Call<RideTrackingDTO> call, Response<RideTrackingDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rideData.postValue(response.body());
                } else {
                    toastMessage.postValue("Failed to load ride data");
                }
            }

            @Override
            public void onFailure(Call<RideTrackingDTO> call, Throwable t) {
                toastMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void rateDriver(long rideId, int rating, String comment) {
        ReviewRequestDTO request = new ReviewRequestDTO(rating, comment);
        ApiProvider.review().rateDriver(rideId, request).enqueue(new Callback<ReviewResponseDTO>() {
            @Override
            public void onResponse(Call<ReviewResponseDTO> call, Response<ReviewResponseDTO> response) {
                if (response.isSuccessful()) {
                    toastMessage.postValue("Driver rated successfully!");
                    driverRated.postValue(true);
                } else {
                    toastMessage.postValue("Failed to rate driver: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponseDTO> call, Throwable t) {
                toastMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }

    public void rateVehicle(long rideId, int rating, String comment) {
        ReviewRequestDTO request = new ReviewRequestDTO(rating, comment);
        ApiProvider.review().rateVehicle(rideId, request).enqueue(new Callback<ReviewResponseDTO>() {
            @Override
            public void onResponse(Call<ReviewResponseDTO> call, Response<ReviewResponseDTO> response) {
                if (response.isSuccessful()) {
                    toastMessage.postValue("Vehicle rated successfully!");
                    vehicleRated.postValue(true);
                } else {
                    toastMessage.postValue("Failed to rate vehicle: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponseDTO> call, Throwable t) {
                toastMessage.postValue("Network error: " + t.getMessage());
            }
        });
    }
}