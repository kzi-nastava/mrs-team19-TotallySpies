package com.ftn.mobile.presentation.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.Ride;
import com.ftn.mobile.data.model.RideStatus;
import com.ftn.mobile.presentation.adapter.DriverHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class DriverHistoryActivity extends AppCompatActivity {
    ArrayList<Ride> rideModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_history);
        RecyclerView recyclerView = findViewById(R.id.adhRecycleView);
        setUpRideModels();
        DriverHistoryAdapter adapter = new DriverHistoryAdapter(this, rideModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpRideModels() {

        String[] departures = getResources().getStringArray(R.array.mock_departures);
        String[] destinations = getResources().getStringArray(R.array.mock_destinations);
        String[] prices = getResources().getStringArray(R.array.mock_prices);
        String[] datesStart = getResources().getStringArray(R.array.mock_dates);
        String[] datesEnd = getResources().getStringArray(R.array.mock_dates);
        String[] timesStart = getResources().getStringArray(R.array.mock_times_start);
        String[] timesEnd = getResources().getStringArray(R.array.mock_times_end);
        String[] panics = getResources().getStringArray(R.array.mock_panics);
        String[] statuses = getResources().getStringArray(R.array.mock_statuses);
        String[] passengers = getResources().getStringArray(R.array.mock_passengers_list);

        for (int i = 0; i < departures.length; i++) {
            boolean isPanic = Boolean.parseBoolean(panics[i]);

            RideStatus status = RideStatus.FINISHED;
            if (statuses[i].equals("DRIVER_CANCEL")) status = RideStatus.CANCELLED_BY_DRIVER;
            else if (statuses[i].equals("PASSENGER_CANCEL")) status = RideStatus.CANCELLED_BY_PASSENGER;

            String[] splitPassengers = passengers[i].split(",");
            List<String> passengerList = new ArrayList<>();
            List<Integer> passengerImagesList = new ArrayList<>();

            for (String p : splitPassengers) {
                passengerList.add(p.trim());
                passengerImagesList.add(R.drawable.ic_passenger_placeholder);
            }

            rideModels.add(new Ride(
                    departures[i],
                    destinations[i],
                    prices[i],
                    datesStart[i],
                    datesEnd[i],
                    timesStart[i],
                    timesEnd[i],
                    isPanic,
                    status,
                    passengerList,
                    passengerImagesList
            ));
        }





    }
}