package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.rides.AdminRideHistoryDTO;
import com.ftn.mobile.data.remote.dto.rides.PassengerRideHistoryDTO;
import com.ftn.mobile.data.remote.dto.rides.RideStopDTO;

import java.util.List;

public class AdminRideHistoryAdapter extends RecyclerView.Adapter<AdminRideHistoryAdapter.AdminRideHistoryViewHolder> {
    public interface OnViewDetailsClickListener {
        void onViewDetails(AdminRideHistoryDTO ride);
    }
    private final Context context;
    private final List<AdminRideHistoryDTO> rides;
    private final AdminRideHistoryAdapter.OnViewDetailsClickListener listener;

    public AdminRideHistoryAdapter(Context context, List<AdminRideHistoryDTO> rides,
                                       AdminRideHistoryAdapter.OnViewDetailsClickListener listener) {
        this.context = context;
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminRideHistoryAdapter.AdminRideHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_admin_ride_history, parent, false);
        return new AdminRideHistoryAdapter.AdminRideHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminRideHistoryAdapter.AdminRideHistoryViewHolder holder, int position) {
        AdminRideHistoryDTO ride = rides.get(position);
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
        if(ride.isCancelled() == true)
            holder.tvIsCancelled.setText("true");
        else holder.tvIsCancelled.setText("false");
        holder.tvTotalPrice.setText(String.valueOf(ride.getTotalPrice()));
        holder.tvUserWhoCancelled.setText(safe(ride.getUserWhoCancelled()));
        if(ride.isPanic() == true)
            holder.tvIsPanic.setText("true");
        else holder.tvIsPanic.setText("false");

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) listener.onViewDetails(ride);
        });
    }
    static class AdminRideHistoryViewHolder extends RecyclerView.ViewHolder{
        TextView tvRoute, tvStartTime, tvEndTime, tvCreationTime, tvIsCancelled, tvUserWhoCancelled, tvTotalPrice, tvIsPanic;
        Button btnViewDetails;
        public AdminRideHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            tvCreationTime = itemView.findViewById(R.id.tvCreationTime);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            tvIsCancelled = itemView.findViewById(R.id.tvIsCancelled);
            tvUserWhoCancelled = itemView.findViewById(R.id.tvUserWhoCancelled);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvIsPanic = itemView.findViewById(R.id.tvIsPanic);
        }
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

}

