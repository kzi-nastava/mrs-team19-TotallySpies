package com.ftn.mobile.presentation.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.ActiveRideDTO;
import com.ftn.mobile.data.remote.dto.VehicleDisplayInfoDTO;
import com.ftn.mobile.presentation.viewModel.AdminTrackingViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class AdminTrackingFragment extends Fragment {
    private MapView map;
    private AdminTrackingViewModel viewModel;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private Map<Long, Marker> vehicleMarkers = new HashMap<>();
    private String currentQuery = "";
    private CompositeDisposable disposable = new CompositeDisposable();
    private StompClient mStompClient;
    private Gson gson = new Gson();

    private TextView txtName, txtStatus, txtFrom, txtTo, txtPrice, txtPassengers, txtStartTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_tracking, container, false);
        map = v.findViewById(R.id.mapAdmin);
        setupMap();
        initUI(v);
        return v;
    }

    private void initUI(View v) {
        View bottomSheet = v.findViewById(R.id.rideDetailsBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        txtName = v.findViewById(R.id.txtDriverName);
        txtStatus = v.findViewById(R.id.txtRideStatus);
        txtFrom = v.findViewById(R.id.txtFrom);
        txtTo = v.findViewById(R.id.txtTo);
        txtPrice = v.findViewById(R.id.txtPrice);
        txtStartTime = v.findViewById(R.id.txtStartTime);
        txtPassengers = v.findViewById(R.id.txtPassengerName);

        androidx.appcompat.widget.SearchView searchView = v.findViewById(R.id.searchDriver);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText.toLowerCase();
                refreshMarkers(); // filter while typing
                return true;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdminTrackingViewModel.class);

        viewModel.getVehicles().observe(getViewLifecycleOwner(), this::refreshMarkers);

        viewModel.fetchInitialData();
        subscribeToWebSocket();
    }

    private void refreshMarkers(List<VehicleDisplayInfoDTO> vehicleList) {
        if (vehicleList == null) return;

        for (VehicleDisplayInfoDTO v : vehicleList) {
            String driverName = v.getDriverName() != null ? v.getDriverName().toLowerCase() : "";

            boolean isVisible = driverName.contains(currentQuery.toLowerCase().trim());

            if (vehicleMarkers.containsKey(v.getId())) {
                Marker m = vehicleMarkers.get(v.getId());
                if (isVisible) {
                    m.setPosition(new GeoPoint(v.getCurrentLat(), v.getCurrentLng()));
                    m.setAlpha(1.0f);
                } else {
                    m.setAlpha(0.0f); // hide
                }
            } else {
                Marker m = new Marker(map);
                m.setId(String.valueOf(v.getId()));
                m.setPosition(new GeoPoint(v.getCurrentLat(), v.getCurrentLng()));
                m.setIcon(ContextCompat.getDrawable(requireContext(),
                        v.isBusy() ? R.drawable.ic_red_car : R.drawable.ic_green_car));

                m.setOnMarkerClickListener((marker, mapView) -> {
                    onVehicleClicked(v);
                    return true;
                });

                if (!isVisible) m.setAlpha(0.0f);

                vehicleMarkers.put(v.getId(), m);
                map.getOverlays().add(m);
            }
        }
        map.invalidate();
    }
    private void refreshMarkers() {
        if (viewModel.getVehicles().getValue() != null) {
            refreshMarkers(viewModel.getVehicles().getValue());
        }
    }

    private void onVehicleClicked(VehicleDisplayInfoDTO v) {
        ActiveRideDTO detail = null;
        if (viewModel.getActiveRides().getValue() != null && v.getDriverId() != null) {
            for (ActiveRideDTO r : viewModel.getActiveRides().getValue()) {
                if (v.getDriverId().equals(r.getDriverId())) {
                    detail = r;
                    break;
                }
            }
        }

        if (detail != null) {
            setDetailsVisibility(View.VISIBLE);
            populateBottomSheet(detail);
        } else {
            txtName.setText(v.getDriverName() != null ? v.getDriverName() : "N/A");
            txtStatus.setText("FREE");
            setDetailsVisibility(View.GONE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        map.getController().animateTo(new GeoPoint(v.getCurrentLat(), v.getCurrentLng()));
    }

    private void populateBottomSheet(ActiveRideDTO ride) {
        txtName.setText(ride.getDriverName());
        txtFrom.setText("From: " + ride.getStartLocation());
        txtTo.setText("To: " + ride.getEndLocation());
        txtPrice.setText("Pricde: " + ride.getPrice() + " RSD");
        txtPassengers.setText("Passengers: " + String.join(", ", ride.getPassengers()));
        if (ride.getStartTime() != null) {
            String formattedTime = formatDateTime(ride.getStartTime());
            txtStartTime.setText("Start: " + formattedTime);
        } else {
            txtStartTime.setText("Start: -");
        }
        if (ride.isPanicPressed()) {
            txtStatus.setText("Panic");
            txtStatus.setTextColor(Color.RED);
        } else {
            txtStatus.setText("ACTIVE");
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void subscribeToWebSocket() {
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/ws-transport/websocket");
        mStompClient.connect();

        disposable.add(mStompClient.topic("/topic/vehicle-locations").subscribe(msg -> {
            Type listType = new TypeToken<ArrayList<VehicleDisplayInfoDTO>>(){}.getType();
            List<VehicleDisplayInfoDTO> updates = gson.fromJson(msg.getPayload(), listType);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> viewModel.updateVehicleData(updates));
            }
        }, throwable -> Log.e("WS_ERROR", "Error", throwable)));
    }

    private void setupMap() {
        Context context = requireContext();
        Configuration.getInstance().load(context,
                context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.getController().setCenter(new GeoPoint(45.2671, 19.8335));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
        if (mStompClient != null) mStompClient.disconnect();
    }

    private void setDetailsVisibility(int visibility) {
        txtFrom.setVisibility(visibility);
        txtTo.setVisibility(visibility);
        txtPrice.setVisibility(visibility);
        txtPassengers.setVisibility(visibility);
        txtStartTime.setVisibility(visibility);
    }

    private String formatDateTime(String rawDate) {
        try {
            String cleanedDate = rawDate.substring(0, 19).replace("T", " ");
            String timeOnly = cleanedDate.substring(11, 16);
            return timeOnly;
        } catch (Exception e) {
            return rawDate;
        }
    }
}
