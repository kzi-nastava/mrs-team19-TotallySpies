package com.ftn.mobile.presentation.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.Ride;
import com.ftn.mobile.data.model.RideStatus;
import com.ftn.mobile.data.remote.ApiProvider;
import com.ftn.mobile.data.remote.dto.DriverRideHistoryDTO;
import com.ftn.mobile.presentation.adapter.DriverHistoryAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class DriverHistoryActivity extends AppCompatActivity {
    ArrayList<Ride> rideModels = new ArrayList<>();
    DriverHistoryAdapter adapter;
    private String selectedFromDate = null;
    private String selectedToDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_history);
        RecyclerView recyclerView = findViewById(R.id.adhRecycleView);
        adapter = new DriverHistoryAdapter(this, rideModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button btnFilter = findViewById(R.id.btnFilterHistory);
        btnFilter.setOnClickListener(v -> {
            showDateRangePicker();
        });
        loadHistoryFromServer(null, null);
    }

    private void showDateRangePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog fromDatePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedFromDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);

            DatePickerDialog toDatePicker = new DatePickerDialog(this, (view1, year1, month1, dayOfMonth1) -> {
                selectedToDate = String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth1);

                loadHistoryFromServer(selectedFromDate, selectedToDate);

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            toDatePicker.setTitle("Select To Date");
            toDatePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        fromDatePicker.setTitle("Select From Date");
        fromDatePicker.show();
    }

    private void loadHistoryFromServer(String from, String to) {

        ApiProvider.driver().getDriverHistory(from, to).enqueue(new Callback<List<DriverRideHistoryDTO>>() {
            @Override
            public void onResponse(Call<List<DriverRideHistoryDTO>> call, Response<List<DriverRideHistoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rideModels.clear();
                    for (DriverRideHistoryDTO dto : response.body()) {
                        rideModels.add(mapDtoToModel(dto));
                    }
                    adapter.notifyDataSetChanged();

                    if (rideModels.isEmpty()) {
                        Toast.makeText(DriverHistoryActivity.this, "No rides found for this range", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.e("API_ERROR", "Response code: " + response.code());
                    if (response.code() == 403 || response.code() == 401) {
                        Toast.makeText(DriverHistoryActivity.this, "Session expired, please login again", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DriverRideHistoryDTO>> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
    private Ride mapDtoToModel(DriverRideHistoryDTO dto) {
        String dateStart = "";
        String timeStart = "";

        if (dto.getStartTime() != null && dto.getStartTime().contains("T")) {
            String[] parts = dto.getStartTime().split("T");
            dateStart = parts[0]; // we get "2026-01-26"
            timeStart = parts[1].substring(0, 5); // we get "16:55"
        }

        String dateEnd = "";
        String timeEnd = "";
        if (dto.getEndTime() != null && dto.getEndTime().contains("T")) {
            String[] parts = dto.getEndTime().split("T");
            dateEnd = parts[0];
            timeEnd = parts[1].substring(0, 5);
        }

        RideStatus status = RideStatus.FINISHED;
        if (dto.isCancelled()) {
            status = RideStatus.CANCELLED_BY_PASSENGER;
        }

        // for now we have placeholders
        List<Integer> images = new ArrayList<>();
        if (dto.getPassengers() != null) {
            for (int i = 0; i < dto.getPassengers().size(); i++) {
                images.add(R.drawable.ic_passenger_placeholder);
            }
        }

        String startLoc = dto.getStartLocation() != null ? dto.getStartLocation() : "Unknown";
        String endLoc = dto.getEndLocation() != null ? dto.getEndLocation() : "Unknown";

        return new Ride(
                startLoc,
                endLoc,
                String.valueOf(dto.getPrice()),
                dateStart,
                dateEnd,
                timeStart,
                timeEnd,
                dto.isPanicPressed(),
                status,
                dto.getPassengers(),
                images
        );
    }
}