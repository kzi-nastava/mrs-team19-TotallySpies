package com.ftn.mobile.presentation.fragments;

import android.app.AlertDialog;
import android.location.Address;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ftn.mobile.BuildConfig;
import com.ftn.mobile.R;
import com.ftn.mobile.data.model.VehicleType;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.CreateRideRequestDTO;
import com.ftn.mobile.data.remote.dto.CreateRideResponseDTO;
import com.ftn.mobile.data.remote.dto.RideStopDTO;
import com.ftn.mobile.data.remote.dto.RoutePointDTO;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideOrderingFragment extends Fragment {

    private MapView map;
    private EditText etStart, etEnd;
    private LinearLayout stopsContainer, emailsContainer, layoutOrder;
    private TextView tvPriceInfo;
    private Button btnAddStop, btnAddEmail, btnSearch, btnOrder;
    private View bottomSheet;
    private RadioButton rbVan, rbLuxury, rbStandard;
    private CheckBox cbBaby, cbPet;

    private AlertDialog searchDialog;
    private TextView tvSearchStatus;
    private ProgressBar progressSearch;
    private Button btnDismissDialog;
    private List<RideStopDTO> confirmedLocations = new ArrayList<>();
    private List<GeoPoint> routePoints = new ArrayList<>();
    private double calculatedDistance = 0;
    private double calculatedTime = 0;

    private java.time.LocalDateTime selectedScheduleTime = null;

    private int textColor;
    private int hintColor;


    public RideOrderingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_ordering, container, false);

        map = view.findViewById(R.id.map);
        etStart = view.findViewById(R.id.etStart);
        etEnd = view.findViewById(R.id.etEnd);

        stopsContainer = view.findViewById(R.id.stopsContainer);
        emailsContainer = view.findViewById(R.id.emailsContainer);
        layoutOrder = view.findViewById(R.id.layoutOrder);

        tvPriceInfo = view.findViewById(R.id.tvPriceInfo);

        btnAddStop = view.findViewById(R.id.btnAddStop);
        btnAddEmail = view.findViewById(R.id.btnAddEmail);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnOrder = view.findViewById(R.id.btnOrder);

        bottomSheet = view.findViewById(R.id.bottomSheet);

        rbLuxury = view.findViewById(R.id.rbLuxury);
        rbStandard = view.findViewById(R.id.rbStandard);
        rbVan = view.findViewById(R.id.rbVan);

        cbBaby = view.findViewById(R.id.cbBaby);
        cbPet = view.findViewById(R.id.cbPet);

        textColor = ContextCompat.getColor(getContext(), R.color.text);
        hintColor = ContextCompat.getColor(getContext(), R.color.text_hint);

        setupMap();

        btnAddStop.setOnClickListener(v -> addStopField());
        btnAddEmail.setOnClickListener(v -> addEmailField());

        btnSearch.setOnClickListener(v -> executeSearchRoute());
        btnOrder.setOnClickListener(v -> createRideRequest());

        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

        RadioButton rbLater = view.findViewById(R.id.rbLater);
        Button btnPickTime = view.findViewById(R.id.btnPickTime);

        rbLater.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnPickTime.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                selectedScheduleTime = null;
                btnOrder.setText("BOOK NOW");
            }
        });

        btnPickTime.setOnClickListener(v -> showTimePicker());

        return view;
    }

    // podesavanje mape
    private void setupMap() {
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.getController().setCenter(new GeoPoint(45.2671, 19.8335));
    }

    private void addStopField(){
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView dragHandle = new TextView(getContext());
        dragHandle.setText(" ☰ ");
        dragHandle.setTextSize(22);
        dragHandle.setPadding(10, 20, 20, 20);
        dragHandle.setTextColor(textColor);

        EditText et = new EditText(getContext());
        et.setHint("Enter stop address");
        et.setTextColor(textColor);
        et.setHintTextColor(hintColor);
        et.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Button btnRemove = new Button(getContext());
        btnRemove.setText("X");
        btnRemove.setTextColor(android.graphics.Color.RED);
        btnRemove.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnRemove.setOnClickListener(v -> stopsContainer.removeView(row));

        row.addView(dragHandle);
        row.addView(et);
        row.addView(btnRemove);

        dragHandle.setOnLongClickListener(v -> {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(row);
            row.startDragAndDrop(null, shadowBuilder, row, 0);
            return true;
        });

        stopsContainer.setOnDragListener((v, event) -> {
            if (event.getAction() == android.view.DragEvent.ACTION_DROP) {
                View draggedRow = (View) event.getLocalState();
                stopsContainer.removeView(draggedRow);

                float dropY = event.getY();
                int newIndex = stopsContainer.getChildCount();
                for (int i = 0; i < stopsContainer.getChildCount(); i++) {
                    if (dropY < stopsContainer.getChildAt(i).getY() + (stopsContainer.getChildAt(i).getHeight() / 2.0)) {
                        newIndex = i;
                        break;
                    }
                }
                stopsContainer.addView(draggedRow, newIndex);
            }
            return true;
        });

        stopsContainer.addView(row);
    }

    private void addEmailField() {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        EditText et = new EditText(getContext());
        et.setHint("Enter stop address");
        et.setTextColor(textColor);
        et.setHintTextColor(hintColor);
        et.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Button btnRemove = new Button(getContext());
        btnRemove.setText("X");
        btnRemove.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        btnRemove.setBackgroundColor(android.R.color.transparent);
        btnRemove.setOnClickListener(v -> emailsContainer.removeView(row));

        row.addView(et);
        row.addView(btnRemove);
        emailsContainer.addView(row);
    }

    // pretraga, geocoding
    private void executeSearchRoute() {
        // sve adrese u listu
        if (!isInputValid()) return;
        List<String> addresses = new ArrayList<>();

        addresses.add(etStart.getText().toString());
        for(int i=0; i<stopsContainer.getChildCount(); i++) {
            View row = stopsContainer.getChildAt(i);
            if (row instanceof LinearLayout) {
                EditText et = (EditText) ((LinearLayout) row).getChildAt(1);
                if(!et.getText().toString().isEmpty()) addresses.add(et.getText().toString());
            }
        }
        addresses.add(etEnd.getText().toString());

        // pokreni background thread
        // Android ne dozvoljava mrezu na UI threadu
        new Thread(() -> {
            try {
                GeocoderNominatim geocoder = new GeocoderNominatim(BuildConfig.APPLICATION_ID);
                confirmedLocations.clear();
                ArrayList<GeoPoint> waypoints = new ArrayList<>();
                int indexCounter = 0; // Brojač za orderIndex

                for (String addr : addresses) {
                    List<Address> found = geocoder.getFromLocationName(addr, 1);
                    if (!found.isEmpty()) {
                        Address a = found.get(0);

                        RideStopDTO stop = new RideStopDTO(a.getLatitude(), a.getLongitude(), addr);
                        stop.setOrderIndex(indexCounter);
                        confirmedLocations.add(stop);
                        waypoints.add(new GeoPoint(a.getLatitude(), a.getLongitude()));

                        indexCounter++;
                    }
                }

                if (waypoints.size() < 2) return;

                RoadManager roadManager = new OSRMRoadManager(getContext(), "MyUserAgent");
                Road road = roadManager.getRoad(waypoints);

                routePoints = road.mRouteHigh;
                calculatedDistance = road.mLength; // km
                calculatedTime = road.mDuration / 60; // min

                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

                getActivity().runOnUiThread(() -> {
                    map.getOverlays().clear();
                    map.getOverlays().add(roadOverlay);
                    map.invalidate(); // osvjezi mapu

                    calculateAndShowPrice();
                    layoutOrder.setVisibility(View.VISIBLE);

                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void calculateAndShowPrice() {
        double basePrice = 300; // standard
        if(rbLuxury.isChecked()) basePrice = 800;
        else if(rbVan.isChecked()) basePrice = 500;

        double price = basePrice + (calculatedDistance * 120);
        tvPriceInfo.setText(String.format("%.1f km • %.0f min • %.0f RSD", calculatedDistance, calculatedTime, price));
    }

    private void createRideRequest() {
        if (!isInputValid()) return;

        RadioButton rbLater = getView().findViewById(R.id.rbLater);
        if (rbLater != null && rbLater.isChecked()) {
            if (selectedScheduleTime == null) {
                android.widget.Toast.makeText(getContext(), "Please set time for later ride", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (selectedScheduleTime.isAfter(now.plusHours(5))) {
                android.widget.Toast.makeText(getContext(), "Ride can be scheduled max 5 hours ahead", android.widget.Toast.LENGTH_LONG).show();
                return;
            }
            if (selectedScheduleTime.isBefore(now)) {
                android.widget.Toast.makeText(getContext(), "Scheduled time cannot be in the past", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
        }

        List<String> emails = new ArrayList<>();
        for(int i=0; i<emailsContainer.getChildCount(); i++) {
            View row = emailsContainer.getChildAt(i);
            if (row instanceof LinearLayout) {
                EditText et = (EditText) ((LinearLayout) row).getChildAt(0);
                if(!et.getText().toString().isEmpty()) emails.add(et.getText().toString());
            }
        }

        VehicleType vType = VehicleType.STANDARD;
        if(rbLuxury.isChecked()) vType = VehicleType.LUXURY;
        if(rbVan.isChecked()) vType = VehicleType.VAN;

        List<RoutePointDTO> path = new ArrayList<>();
        for(GeoPoint gp : routePoints) {
            RoutePointDTO p = new RoutePointDTO();
            p.setLat(gp.getLatitude());
            p.setLng(gp.getLongitude());
            path.add(p);
        }

        double distanceRounded = Math.round(calculatedDistance * 100.0) / 100.0;
        double timeRounded = Math.round(calculatedTime * 100.0) / 100.0;

        CreateRideRequestDTO dto = new CreateRideRequestDTO();
        dto.setLocations(confirmedLocations);
        dto.setPassengerEmails(emails);
        dto.setVehicleType(vType);
        dto.setBabyTransport(cbBaby.isChecked());
        dto.setPetTransport(cbPet.isChecked());
        dto.setDistanceKm(distanceRounded);
        dto.setEstimatedTime(timeRounded);
        dto.setPath(path);
        dto.setScheduledFor(selectedScheduleTime);

        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapter(java.time.LocalDateTime.class, (com.google.gson.JsonSerializer<java.time.LocalDateTime>)
                        (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .setPrettyPrinting()
                .create();

        String dtoJson = gson.toJson(dto);
        android.util.Log.d("RIDE_DTO", dtoJson);

        showSearchingDialog();

        ApiProvider.ride().createRide(dto).enqueue(new Callback<CreateRideResponseDTO>() {
            @Override
            public void onResponse(Call<CreateRideResponseDTO> call, Response<CreateRideResponseDTO> response) {
                if(response.isSuccessful() && response.body() != null) {
                    updateDialogStatus("FOUND", response.body().getMessage());
                } else {
                    String errorMsg = "No drivers available.";
                    try {
                        String errorBody = response.errorBody().string();
                        JsonObject jsonObject = JsonParser.parseString(errorBody).getAsJsonObject();
                        if(jsonObject.has("message")) errorMsg = jsonObject.get("message").getAsString();
                    } catch (Exception e) {

                    }
                    updateDialogStatus("NOT_FOUND", errorMsg);
                }
            }
            @Override
            public void onFailure(Call<CreateRideResponseDTO> call, Throwable t) {
                updateDialogStatus("ERROR", "Network error: " + t.getMessage());
            }
        });
    }

    private void showSearchingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dView = getLayoutInflater().inflate(R.layout.dialog_driver_search, null);

        tvSearchStatus = dView.findViewById(R.id.tvSearchStatus);
        progressSearch = dView.findViewById(R.id.progressSearch);
        btnDismissDialog = dView.findViewById(R.id.btnDismissDialog);

        btnDismissDialog.setOnClickListener(v -> searchDialog.dismiss());

        builder.setView(dView);
        builder.setCancelable(false);
        searchDialog = builder.create();

        if (searchDialog.getWindow() != null) {
            searchDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        searchDialog.show();
    }

    private void updateDialogStatus(String status, String msg) {
        if (searchDialog == null || !searchDialog.isShowing()) return;

        getActivity().runOnUiThread(() -> {
            tvSearchStatus.setText(msg);
            progressSearch.setVisibility(View.GONE);
            btnDismissDialog.setVisibility(View.VISIBLE);

            if (status.equals("FOUND")) {
                tvSearchStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvSearchStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        });
    }

    private boolean isInputValid() {
        if (etStart.getText().toString().trim().isEmpty()) {
            etStart.setError("Start is required");
            return false;
        }
        if (etEnd.getText().toString().trim().isEmpty()) {
            etEnd.setError("Destination is required");
            return false;
        }
        return true;
    }

    private void showTimePicker() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        new android.app.TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            java.time.LocalDateTime nowLDT = java.time.LocalDateTime.now();
            selectedScheduleTime = java.time.LocalDateTime.of(
                    nowLDT.getYear(), nowLDT.getMonth(), nowLDT.getDayOfMonth(), hourOfDay, minute);

            if (selectedScheduleTime.isBefore(nowLDT)) {
                selectedScheduleTime = selectedScheduleTime.plusDays(1);
            }

            btnOrder.setText("BOOK FOR " + String.format("%02d:%02d", hourOfDay, minute));
        }, now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE), true).show();
    }
}