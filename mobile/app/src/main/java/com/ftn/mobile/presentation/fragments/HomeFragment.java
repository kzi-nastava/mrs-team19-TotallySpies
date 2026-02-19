package com.ftn.mobile.presentation.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.VehicleDisplayInfoDTO;
import com.ftn.mobile.presentation.viewModel.HomeViewModel;
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

public class HomeFragment extends Fragment {
    private MapView map;
    private Map<Long, Marker> vehicleMarkers = new HashMap<>();
    private HomeViewModel viewModel;

    private StompClient mStompClient;
    private Gson gson = new Gson();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        map = v.findViewById(R.id.map);
        setupMap();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.getVehicles().observe(getViewLifecycleOwner(), vehicles -> {
            if (vehicles != null) {
                updateMarkers(vehicles);
            }
        });

        viewModel.fetchInitialVehicles();
        subscribeToVehicleUpdates();
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

    private void updateMarkers(List<VehicleDisplayInfoDTO> vehicles) {
        for (VehicleDisplayInfoDTO v : vehicles) {
            GeoPoint position = new GeoPoint(v.getCurrentLat(), v.getCurrentLng());

            if (vehicleMarkers.containsKey(v.getId())) {
                Marker m = vehicleMarkers.get(v.getId());
                m.setPosition(position);
                m.setIcon(v.isBusy() ?
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_car) :
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_green_car));
            } else {
                Marker m = new Marker(map);
                m.setId(String.valueOf(v.getId()));
                m.setPosition(position);
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                m.setIcon(v.isBusy() ?
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_car) :
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_green_car));
                m.setTitle(String.valueOf(v.getId()));

                vehicleMarkers.put(v.getId(), m);
                map.getOverlays().add(m);
            }
        }
        map.invalidate();
    }

    private void subscribeToVehicleUpdates() {
        String wsUrl = "ws://10.0.2.2:8080/ws-transport/websocket";
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
        compositeDisposable.add(mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED: Log.d("STOMP", "Stomp connection opened"); break;
                case ERROR: Log.e("STOMP", "Error connection", lifecycleEvent.getException()); break;
                case CLOSED: Log.d("STOMP", "Stomp connection closed"); break;
            }
        }));

        mStompClient.connect();

        compositeDisposable.add(mStompClient.topic("/topic/vehicle-locations").subscribe(topicMessage -> {
            Log.d("STOMP_DEBUG", "arrived!");
            String json = topicMessage.getPayload();
            Type listType = new TypeToken<ArrayList<VehicleDisplayInfoDTO>>(){}.getType();
            List<VehicleDisplayInfoDTO> updates = gson.fromJson(json, listType);

            viewModel.updateVehicleData(updates);

        }, throwable -> Log.e("STOMP", "Error on subscribe", throwable)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.clear();
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
    }
}