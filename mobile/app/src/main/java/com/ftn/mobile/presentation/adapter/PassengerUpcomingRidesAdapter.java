package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.PassengerUpcomingRideDTO;
import com.ftn.mobile.data.remote.dto.RideStopDTO;

import java.util.Collections;
import java.util.List;

public class PassengerUpcomingRidesAdapter extends RecyclerView.Adapter<PassengerUpcomingRidesAdapter.ViewHolder>{
    private List<PassengerUpcomingRideDTO> rides;
    private LayoutInflater inflater;

    public PassengerUpcomingRidesAdapter(Context context, List<PassengerUpcomingRideDTO> rides) {
        this.rides = rides;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_passenger_upcoming_ride, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PassengerUpcomingRideDTO ride = rides.get(position);

        holder.txtPrice.setText(ride.getPrice() + " RSD");
        holder.txtStatus.setText(ride.getStatus().toString());
        holder.txtDriverName.setText("Driver: " + (ride.getDriverName() != null ? ride.getDriverName() : "Not assigned"));

        holder.stopsContainer.removeAllViews();
        List<RideStopDTO> stops = ride.getLocations();

        Collections.sort(stops, (a, b) -> Integer.compare(a.getOrderIndex(), b.getOrderIndex()));

        for (RideStopDTO stop : stops) {
            View stopView = inflater.inflate(R.layout.item_ride_stop_upcoming, holder.stopsContainer, false);
            TextView addr = stopView.findViewById(R.id.txtAddress);
            addr.setText(stop.getAddress());

            holder.stopsContainer.addView(stopView);
        }
    }

    @Override
    public int getItemCount() { return rides.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPrice, txtStatus, txtDriverName;
        LinearLayout stopsContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtDriverName = itemView.findViewById(R.id.txtDriverName);
            stopsContainer = itemView.findViewById(R.id.stopsContainer);
        }
    }
}
