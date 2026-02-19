package com.ftn.mobile.presentation.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideDetailsResponseDTO;
import com.ftn.mobile.data.remote.dto.rides.RideGradeDTO;
import com.ftn.mobile.data.remote.dto.rides.RideStopDTO;
import com.ftn.mobile.presentation.adapter.RideGradesAdapter;
import com.ftn.mobile.presentation.adapter.RideReportsAdapter;
import com.ftn.mobile.presentation.adapter.RideStopsAdapter;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideDetailsFragment extends Fragment {

    private static final String ARG_RIDE_ID = "ride_id";

    private long rideId;

    private MapView map;
    private TextView tvDriver, tvEmail, tvPhone, tvDistance, tvPrice;
    private Button btnReorder;
    private ProgressBar progress;

    private RecyclerView rvStops, rvGrades, rvReports;

    private final ArrayList<RideStopDTO> stops = new ArrayList<>();
    private final ArrayList<RideGradeDTO> grades = new ArrayList<>();
    private final ArrayList<RideReportsAdapter.ReportItem> reports = new ArrayList<>();

    private RideStopsAdapter stopsAdapter;
    private RideGradesAdapter gradesAdapter;
    private RideReportsAdapter reportsAdapter;

    public static PassengerRideDetailsFragment newInstance(long rideId) {
        PassengerRideDetailsFragment f = new PassengerRideDetailsFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_RIDE_ID, rideId);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rideId = getArguments() != null ? getArguments().getLong(ARG_RIDE_ID, -1) : -1;

        // map
        map = view.findViewById(R.id.map);
        setupMap();

        // texts
        tvDriver = view.findViewById(R.id.tvDriverFullName);
        tvEmail = view.findViewById(R.id.tvDriverEmail);
        tvPhone = view.findViewById(R.id.tvDriverPhone);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvPrice = view.findViewById(R.id.tvTotalPrice);

        // progress
        progress = view.findViewById(R.id.progress);
        //buttons
        btnReorder = view.findViewById(R.id.btnReorder);
        // recyclers
        rvStops = view.findViewById(R.id.rvStops);
        rvGrades = view.findViewById(R.id.rvGrades);
        rvReports = view.findViewById(R.id.rvReports);

        stopsAdapter = new RideStopsAdapter(requireContext(), stops);
        gradesAdapter = new RideGradesAdapter(requireContext(), grades);
        reportsAdapter = new RideReportsAdapter(requireContext(), reports);

        rvStops.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvStops.setAdapter(stopsAdapter);

        rvGrades.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGrades.setAdapter(gradesAdapter);
        rvReports.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReports.setAdapter(reportsAdapter);

        if (rideId == -1) {
            Toast.makeText(requireContext(), "Invalid ride id", Toast.LENGTH_SHORT).show();
            return;
        }
        loadDetails(rideId);
        btnReorder.setOnClickListener(v -> reorderRide());
    }
    private void reorderRide(){
        RideOrderingFragment rideOrderingFragment = RideOrderingFragment.newInstance(rideId);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, rideOrderingFragment)
                .addToBackStack(null)
                .commit();

    }

    private void setupMap() {
        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.getController().setCenter(new GeoPoint(45.2671, 19.8335)); // Novi Sad default
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        if (map != null) map.onPause();
        super.onPause();
    }

    private void loadDetails(long id) {
        setLoading(true);
        ApiProvider.ride().getRideDetails(id).enqueue(new Callback<PassengerRideDetailsResponseDTO>() {
            @Override
            public void onResponse(Call<PassengerRideDetailsResponseDTO> call,
                                   Response<PassengerRideDetailsResponseDTO> response) {
                if (!isAdded()) return;
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    bind(response.body());
                } else {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PassengerRideDetailsResponseDTO> call, Throwable t) {
                if (!isAdded()) return;

                setLoading(false);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void bind(PassengerRideDetailsResponseDTO details) {
        String fullName = safe(details.getDriverName()) + " " + safe(details.getDriverLastName());
        tvDriver.setText(fullName.trim());
        tvEmail.setText(safe(details.getDriverEmail()));
        tvPhone.setText(safe(details.getDriverPhoneNumber()));
        tvDistance.setText(details.getDistanceKm() + " km");
        tvPrice.setText(String.valueOf(details.getTotalPrice()));
        stops.clear();
        if (details.getRideStops() != null) stops.addAll(details.getRideStops());
        stopsAdapter.notifyDataSetChanged();
        grades.clear();
        if (details.getRideGrades() != null) grades.addAll(details.getRideGrades());
        gradesAdapter.notifyDataSetChanged();
        reports.clear();
        Map<String, String> mapRep = details.getReportReasons();
        if (mapRep != null && !mapRep.isEmpty()) {
            for (Map.Entry<String, String> e : mapRep.entrySet()) {
                reports.add(new RideReportsAdapter.ReportItem(e.getKey(), e.getValue()));
            }
        }
        reportsAdapter.notifyDataSetChanged();
        drawRouteFromStops(details.getRideStops());
    }
    private void drawRouteFromStops(java.util.List<RideStopDTO> rideStops) {
        if (rideStops == null || rideStops.size() < 2) return;
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        for (RideStopDTO s : rideStops) {
            waypoints.add(new GeoPoint(s.getLatitude(), s.getLongitude()));
        }
        new Thread(() -> {
            try {
                RoadManager roadManager = new OSRMRoadManager(requireContext(), "MyUserAgent");
                Road road = roadManager.getRoad(waypoints);
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    map.getOverlays().clear();
                    map.getOverlays().add(roadOverlay);

                    map.getController().setCenter(waypoints.get(0));
                    map.getController().setZoom(15.0);

                    map.invalidate();
                });

            } catch (Exception e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error drawing route", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void setLoading(boolean loading) {
        if (progress != null) progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }
}

/**package com.ftn.mobile.presentation.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideDetailsResponseDTO;
import com.ftn.mobile.data.remote.dto.rides.RideGradeDTO;
import com.ftn.mobile.data.remote.dto.rides.RideStopDTO;
import com.ftn.mobile.presentation.adapter.RideGradesAdapter;
import com.ftn.mobile.presentation.adapter.RideReportsAdapter;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideDetailsFragment extends Fragment {

    private static final String ARG_RIDE_ID = "ride_id";

    private long rideId;

    private TextView tvDriver, tvEmail, tvPhone, tvDistance, tvPrice;
    private ProgressBar progress;
    private MapView map;
    private RecyclerView rvGrades, rvReports;

    private final ArrayList<RideStopDTO> rideStops = new ArrayList<>();
    private final ArrayList<RideGradeDTO> grades = new ArrayList<>();
    private final ArrayList<RideReportsAdapter.ReportItem> reports = new ArrayList<>();

    private RideGradesAdapter gradesAdapter;
    private RideReportsAdapter reportsAdapter;

    public static PassengerRideDetailsFragment newInstance(long rideId) {
        PassengerRideDetailsFragment f = new PassengerRideDetailsFragment();
        Bundle b = new Bundle();
        b.putLong(ARG_RIDE_ID, rideId);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_details, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rideId = getArguments() != null ? getArguments().getLong(ARG_RIDE_ID, -1) : -1;
        map = view.findViewById(R.id.map);
        setupMap();
        drawRoute();
        tvEmail = view.findViewById(R.id.tvDriverEmail);
        tvPhone = view.findViewById(R.id.tvDriverPhone);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvDriver = view.findViewById(R.id.tvDriverFullName);
        tvPrice = view.findViewById(R.id.tvTotalPrice);
        progress = view.findViewById(R.id.progress);
        rvGrades = view.findViewById(R.id.rvGrades);
        rvReports = view.findViewById(R.id.rvReports);
        gradesAdapter = new RideGradesAdapter(getContext(), grades);
        reportsAdapter = new RideReportsAdapter(getContext(), reports);
        rvGrades.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGrades.setAdapter(gradesAdapter);
        rvReports.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReports.setAdapter(reportsAdapter);

        if (rideId == -1) {
            Toast.makeText(requireContext(), "Invalid ride id", Toast.LENGTH_SHORT).show();
            return;
        }

        loadDetails(rideId);
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
    private void drawRoute(){

    }
    private void loadDetails(long id) {
        setLoading(true);

        ApiProvider.ride().getRideDetails(id).enqueue(new Callback<PassengerRideDetailsResponseDTO>() {
            @Override
            public void onResponse(Call<PassengerRideDetailsResponseDTO> call,
                                   Response<PassengerRideDetailsResponseDTO> response) {
                if (!isAdded()) return;

                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    bind(response.body());
                } else {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PassengerRideDetailsResponseDTO> call, Throwable t) {
                if (!isAdded()) return;

                setLoading(false);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bind(PassengerRideDetailsResponseDTO rideDetails) {
        String fullName = safe(rideDetails.getDriverName()) + " " + safe(rideDetails.getDriverLastName());
        tvDriver.setText(fullName.trim());
        tvEmail.setText(safe(rideDetails.getDriverEmail()));
        tvPhone.setText(safe(rideDetails.getDriverPhoneNumber()));

        tvDistance.setText(String.valueOf(rideDetails.getDistanceKm()) + " km");
        tvPrice.setText(String.valueOf(rideDetails.getTotalPrice()));
        // grades
        grades.clear();
        if (rideDetails.getRideGrades() != null) grades.addAll(rideDetails.getRideGrades());
        gradesAdapter.notifyDataSetChanged();
        // reports
        reports.clear();
        Map<String, String> reportsMap = rideDetails.getReportReasons();
        if (reportsMap != null && !reportsMap.isEmpty()) {
            for (Map.Entry<String, String> e : reportsMap.entrySet()) {
                reports.add(new RideReportsAdapter.ReportItem(e.getKey(), e.getValue()));
            }
        }
        reportsAdapter.notifyDataSetChanged();
    }

    private void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }
}**/