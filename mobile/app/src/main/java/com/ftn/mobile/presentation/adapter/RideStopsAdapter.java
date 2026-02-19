package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.rides.RideStopDTO;

import java.util.List;

public class RideStopsAdapter extends RecyclerView.Adapter<RideStopsAdapter.StopViewHolder> {

    private final Context context;
    private final List<RideStopDTO> stops;

    public RideStopsAdapter(Context context, List<RideStopDTO> stops) {
        this.context = context;
        this.stops = stops;
    }
    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ride_stop, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        RideStopDTO stop = stops.get(position);
        String address = stop.getAddress() != null ? stop.getAddress() : "Unknown";
        int order = position + 1;
        holder.tvStopOrder.setText("Stop " + order);
        holder.tvStopAddress.setText(address);
    }

    @Override
    public int getItemCount() {
        return stops != null ? stops.size() : 0;
    }

    static class StopViewHolder extends RecyclerView.ViewHolder {
        TextView tvStopOrder;
        TextView tvStopAddress;
        public StopViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStopOrder = itemView.findViewById(R.id.tvStopOrder);
            tvStopAddress = itemView.findViewById(R.id.tvStopAddress);
        }
    }
}
