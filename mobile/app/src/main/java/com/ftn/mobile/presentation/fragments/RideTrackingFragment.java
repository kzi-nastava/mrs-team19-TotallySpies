package com.ftn.mobile.presentation.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.InconsistencyReportRequestDTO;
import com.ftn.mobile.data.remote.dto.RideTrackingDTO;
import com.ftn.mobile.data.remote.dto.RoutePointDTO;
import com.ftn.mobile.presentation.viewModel.RideTrackingViewModel;
import com.google.gson.Gson;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class RideTrackingFragment extends Fragment {
    private Long rideId;
    private TextView etaText, driverInfo;
    private MapView map;
    private Marker vehicleMarker;
    private Polyline routePolyline;
    private RideTrackingViewModel viewModel;
    String color = "#6E58C6";
    int routeColor = Color.parseColor(color);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rideId = getArguments().getLong("rideId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ride_tracking, container, false);
        map = v.findViewById(R.id.map);
        etaText = v.findViewById(R.id.etaText);
        driverInfo = v.findViewById(R.id.driverInfoText);
        v.findViewById(R.id.btnReportInconsistency).setOnClickListener(view -> showInconsistencyDialog());
        setupMap();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RideTrackingViewModel.class);
        viewModel.getRideData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                updateUI(data);
            }
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        if (rideId != null) {
            viewModel.initStomp(rideId);
            viewModel.fetchInitialData();
        }
    }
    private void setupMap() {
        Context context = requireContext();
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        Configuration.getInstance().load(context,
                context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(16.0);
    }

    private void updateUI(RideTrackingDTO data) {
        GeoPoint pos = new GeoPoint(data.getVehicleLat(), data.getVehicleLng());
        if (vehicleMarker == null) {
            vehicleMarker = new Marker(map);
            vehicleMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_red_car));
            vehicleMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            map.getOverlays().add(vehicleMarker);
        }
        vehicleMarker.setPosition(pos);

        if (data.getRoutePoints() != null && routePolyline == null) {
            routePolyline = new Polyline();
            routePolyline.getOutlinePaint().setColor(routeColor);
            routePolyline.getOutlinePaint().setStrokeWidth(12f);
            List<GeoPoint> routePath = new ArrayList<>();
            for (RoutePointDTO p : data.getRoutePoints()) {
                routePath.add(new GeoPoint(p.getLat(), p.getLng()));
            }
            routePolyline.setPoints(routePath);
            map.getOverlays().add(0, routePolyline);

            addStationMarker(routePath.get(0), "Pickup", R.drawable.ic_start);

            addStationMarker(routePath.get(routePath.size() - 1), "Destination", R.drawable.ic_end);
        }

        map.getController().animateTo(pos);
        etaText.setText("ETA: " + data.getEta() + " min");
        driverInfo.setText("Driver: " + data.getDriverName() + " (" + data.getCarModel() + ")");
    }

    private void addStationMarker(GeoPoint point, String title, int iconRes) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ContextCompat.getDrawable(requireContext(), iconRes));
        marker.setTitle(title);
        map.getOverlays().add(marker);
    }
    private void showInconsistencyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Report");

        final EditText input = new EditText(getContext());
        input.setHint("Text...");
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String reason = input.getText().toString();
            sendReport(reason);
        });
        builder.setNegativeButton("Cancel", (dialog, i) -> dialog.cancel());

        builder.show();
    }

    private void sendReport(String reason) {
        viewModel.sendReport(rideId, reason);
    }
}