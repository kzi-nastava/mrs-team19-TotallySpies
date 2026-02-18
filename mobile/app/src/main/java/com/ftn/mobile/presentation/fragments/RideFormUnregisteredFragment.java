package com.ftn.mobile.presentation.fragments;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ftn.mobile.BuildConfig;
import com.ftn.mobile.R;
import com.ftn.mobile.presentation.view.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;


public class RideFormUnregisteredFragment extends Fragment {

    private MapView map;
    private EditText etStart, etEnd;
    private Button btnSearch;
    private Button btnChoose;
    private View bottomSheet;

    public RideFormUnregisteredFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ride_form_unregistered, container, false);

        map = view.findViewById(R.id.map);
        etStart = view.findViewById(R.id.etStart);
        etEnd = view.findViewById(R.id.etEnd);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnChoose = view.findViewById(R.id.btnChoose);
        bottomSheet = view.findViewById(R.id.bottomSheet);

        setupMap();

        btnSearch.setOnClickListener(v -> executeSearchRoute());
        btnChoose.setOnClickListener(v -> moveToLogin() );
        return view;
    }
    private void moveToLogin(){
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void setupMap() {
        Configuration.getInstance().load(
                getContext(),
                PreferenceManager.getDefaultSharedPreferences(getContext())
        );

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.getController().setCenter(new GeoPoint(45.2671, 19.8335));
    }

    private void executeSearchRoute() {

        String start = etStart.getText().toString().trim();
        String end = etEnd.getText().toString().trim();

        if (start.isEmpty()) {
            etStart.setError("Starting point required");
            return;
        }

        if (end.isEmpty()) {
            etEnd.setError("Destination required");
            return;
        }

        new Thread(() -> {
            try {
                GeocoderNominatim geocoder =
                        new GeocoderNominatim(BuildConfig.APPLICATION_ID);

                List<Address> startList = geocoder.getFromLocationName(start, 1);
                List<Address> endList = geocoder.getFromLocationName(end, 1);

                if (startList.isEmpty() || endList.isEmpty()) {
                    showToast("Location not found");
                    return;
                }

                Address startAddr = startList.get(0);
                Address endAddr = endList.get(0);

                ArrayList<GeoPoint> waypoints = new ArrayList<>();
                waypoints.add(new GeoPoint(startAddr.getLatitude(), startAddr.getLongitude()));
                waypoints.add(new GeoPoint(endAddr.getLatitude(), endAddr.getLongitude()));

                RoadManager roadManager =
                        new OSRMRoadManager(getContext(), "MyUserAgent");

                Road road = roadManager.getRoad(waypoints);

                Polyline roadOverlay =
                        RoadManager.buildRoadOverlay(road);

                double distance = road.mLength;     // km
                double time = road.mDuration / 60;  // min

                requireActivity().runOnUiThread(() -> {
                    map.getOverlays().clear();
                    map.getOverlays().add(roadOverlay);
                    map.invalidate();

                    Toast.makeText(
                            getContext(),
                            String.format("%.1f km • %.0f min",
                                    distance, time),
                            Toast.LENGTH_LONG
                    ).show();

                    BottomSheetBehavior.from(bottomSheet)
                            .setState(BottomSheetBehavior.STATE_COLLAPSED);
                });

            } catch (Exception e) {
                e.printStackTrace();
                showToast("Error calculating route");
            }
        }).start();
    }

    private void showToast(String msg) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
        );
    }
}