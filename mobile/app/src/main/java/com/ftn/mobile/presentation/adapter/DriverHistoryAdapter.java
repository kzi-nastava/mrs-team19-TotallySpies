package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.Ride;
import com.ftn.mobile.data.model.RideStatus;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class DriverHistoryAdapter extends RecyclerView.Adapter<DriverHistoryAdapter.DHViewHolder> {

    Context context;
    ArrayList<Ride> rides;
    public DriverHistoryAdapter(Context context, ArrayList<Ride> rides) {
        this.context = context;
        this.rides = rides;
    }

    @NonNull
    @Override
    public DriverHistoryAdapter.DHViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new DriverHistoryAdapter.DHViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverHistoryAdapter.DHViewHolder holder, int position) {
        Ride currentRide = rides.get(position);

        holder.tvDeparture.setText(currentRide.getDeparture());
        holder.tvDestination.setText(currentRide.getDestination());
        holder.tvPrice.setText(currentRide.getPrice());

        holder.tvStartDate.setText(currentRide.getDateStart());
        holder.tvEndDate.setText(currentRide.getDateEnd());
        holder.tvStartTime.setText(currentRide.getStartTime());
        holder.tvEndTime.setText(currentRide.getEndTime());


        if (currentRide.isPanic()) {
            holder.imgPanic.setVisibility(View.VISIBLE); // imgPanic je tvoj ImageView za warning
        } else {
            holder.imgPanic.setVisibility(View.GONE);
        }

        if (currentRide.getStatus() == RideStatus.CANCELLED_BY_DRIVER) {
            holder.tvStatus.setText("Driver cancelled this ride");
            holder.tvStatus.setVisibility(View.VISIBLE);
        } else if (currentRide.getStatus() == RideStatus.CANCELLED_BY_PASSENGER) {
            holder.tvStatus.setText("Passenger cancelled this ride");
            holder.tvStatus.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatus.setText("Finished");
            holder.tvStatus.setVisibility(View.VISIBLE);;
        }

        List<String> passengers = currentRide.getPassengers();
        List<Integer> images = currentRide.getPassengersImages();

        // get the first one
        if (passengers != null && !passengers.isEmpty()) {
            holder.tvPassengerName.setText(passengers.get(0));
            holder.imageView.setImageResource(images.get(0));

            // if there isnt more than 1 passenger make arrows invisible
            if (passengers.size() > 1) {
                holder.btnNext.setVisibility(View.VISIBLE);
                holder.btnPrev.setVisibility(View.VISIBLE);

                // setting up click listener
                final int[] currentIndex = {0};
                holder.btnNext.setOnClickListener(v -> {
                    if (currentIndex[0] < passengers.size() - 1) {
                        currentIndex[0]++;
                        holder.tvPassengerName.setText(passengers.get(currentIndex[0]));
                        holder.imageView.setImageResource(images.get(currentIndex[0]));
                    }
                });

                holder.btnPrev.setOnClickListener(v -> {
                    if (currentIndex[0] > 0) {
                        currentIndex[0]--;
                        holder.tvPassengerName.setText(passengers.get(currentIndex[0]));
                        holder.imageView.setImageResource(images.get(currentIndex[0]));
                    }
                });
            } else {
                holder.btnNext.setVisibility(View.INVISIBLE);
                holder.btnPrev.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class DHViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, imgPanic;
        TextView tvDeparture, tvDestination, tvPrice, tvStartDate, tvEndDate,
                tvStartTime, tvEndTime, tvStatus, tvPassengerName;
        ImageButton btnNext, btnPrev;

        public DHViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgPassenger);
            imgPanic = itemView.findViewById(R.id.imgPanic);

            tvDeparture = itemView.findViewById(R.id.txtStartLocation);
            tvDestination = itemView.findViewById(R.id.txtEndLocation);
            tvPrice = itemView.findViewById(R.id.txtPrice);
            tvStartDate = itemView.findViewById(R.id.txtStartDate);
            tvEndDate = itemView.findViewById(R.id.txtEndDate);
            tvStartTime = itemView.findViewById(R.id.txtStartTime);
            tvEndTime = itemView.findViewById(R.id.txtEndTime);
            tvStatus = itemView.findViewById(R.id.txtStatus);
            tvPassengerName = itemView.findViewById(R.id.txtPassengerName);

            btnNext = itemView.findViewById(R.id.btnNextPassenger);
            btnPrev = itemView.findViewById(R.id.btnPrevPassenger);
        }
    }
}
