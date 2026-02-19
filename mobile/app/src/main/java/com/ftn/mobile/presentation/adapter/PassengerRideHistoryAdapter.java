package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.Ride;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideHistoryDTO;
import com.ftn.mobile.data.remote.dto.rides.RideStopDTO;

import java.util.List;

public class PassengerRideHistoryAdapter extends RecyclerView.Adapter<PassengerRideHistoryAdapter.PassengerRideHistoryViewHolder> {
    public interface OnViewDetailsClickListener {
        void onViewDetails(PassengerRideHistoryDTO ride);
    }
    private final Context context;
    private final List<PassengerRideHistoryDTO> rides;
    private final OnViewDetailsClickListener listener;

    public PassengerRideHistoryAdapter(Context context, List<PassengerRideHistoryDTO> rides,
                                       OnViewDetailsClickListener listener) {
        this.context = context;
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PassengerRideHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_passenger_ride_history, parent, false);
        return new PassengerRideHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerRideHistoryViewHolder holder, int position) {
        PassengerRideHistoryDTO ride = rides.get(position);
        List<RideStopDTO> rideStops = ride.getRideStops();
        String pickupAddress = safe(rideStops.get(0).getAddress());
        String destinationAddress = safe(rideStops.get(rideStops.size()-1).getAddress());
        String startTime = safe(ride.getStartedAt());
        String endTime = safe(ride.getFinishedAt());
        String creationTime = safe(ride.getCreatedAt());
        holder.tvRoute.setText(pickupAddress + " → " + destinationAddress);

        holder.tvStartTime.setText("Started at " + startTime);
        holder.tvEndTime.setText("Finished at " + endTime);
        holder.tvCreationTime.setText("Created at " + creationTime);

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) listener.onViewDetails(ride);
        });
    }
    private String safe(String input){
        if (input == null || input.trim().isEmpty())
            return "Unknown";
        else
            return input;
    }
    @Override
    public int getItemCount() {
        return rides != null ? rides.size() : 0;
    }
    static class PassengerRideHistoryViewHolder extends RecyclerView.ViewHolder{
        TextView tvRoute, tvStartTime, tvEndTime, tvCreationTime;
        Button btnViewDetails;
        public PassengerRideHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvCreationTime = itemView.findViewById(R.id.tvCreationTime);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }

    }
}
