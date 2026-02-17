package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.RideStatus;
import com.ftn.mobile.data.remote.dto.ScheduledRideDTO;
import com.ftn.mobile.data.remote.dto.UserProfileResponseDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduledRidesAdapter extends RecyclerView.Adapter<ScheduledRidesAdapter.ActiveViewHolder> {

    private Context context;
    private List<ScheduledRideDTO> rides;
    private OnRideActionListener listener;

    public interface OnRideActionListener {
        void onFinishRide(Long rideId);
    }

    public ScheduledRidesAdapter(Context context, List<ScheduledRideDTO> rides, OnRideActionListener listener) {
        this.context = context;
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ride_action_row, parent, false);
        return new ActiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveViewHolder holder, int position) {
        ScheduledRideDTO currentRide = rides.get(position);

        holder.tvDeparture.setText(currentRide.getStartLocation());
        holder.tvDestination.setText(currentRide.getEndLocation());
        holder.tvPrice.setText(currentRide.getTotalPrice() + " RSD");

        if (currentRide.getDisplayTime() != null) {
            String time = currentRide.getDisplayTime().substring(11, 16);
            holder.tvStartTime.setText(time);
        }

        RideStatus status = currentRide.getStatus();
        holder.tvStatus.setText(status.toString());

        if (status == RideStatus.SCHEDULED) {
            holder.btnStart.setVisibility(View.VISIBLE);
            holder.btnEnd.setVisibility(View.GONE);
            holder.tvStatus.setTextColor(context.getColor(R.color.primary));
        } else if (status == RideStatus.ACTIVE) {
            holder.btnStart.setVisibility(View.GONE);
            holder.btnEnd.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(context.getColor(R.color.accent));
        } else {
            holder.btnStart.setVisibility(View.GONE);
            holder.btnEnd.setVisibility(View.GONE);
        }

        holder.btnEnd.setOnClickListener(v -> listener.onFinishRide(currentRide.getRideId()));

        List<UserProfileResponseDTO> passengers = currentRide.getPassengers();
        if (passengers != null && !passengers.isEmpty()) {
            final int[] currentIndex = {0};

            updatePassengerUI(holder, passengers.get(0));

            if (passengers.size() > 1) {
                holder.btnNext.setVisibility(View.VISIBLE);
                holder.btnPrev.setVisibility(View.VISIBLE);

                holder.btnNext.setOnClickListener(v -> {
                    if (currentIndex[0] < passengers.size() - 1) {
                        currentIndex[0]++;
                        updatePassengerUI(holder, passengers.get(currentIndex[0]));
                    }
                });

                holder.btnPrev.setOnClickListener(v -> {
                    if (currentIndex[0] > 0) {
                        currentIndex[0]--;
                        updatePassengerUI(holder, passengers.get(currentIndex[0]));
                    }
                });
            } else {
                holder.btnNext.setVisibility(View.INVISIBLE);
                holder.btnPrev.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void updatePassengerUI(ActiveViewHolder holder, UserProfileResponseDTO passenger) {
        holder.tvPassengerName.setText(passenger.getName() + " " + passenger.getLastName());
        // load pictures
        holder.imageView.setImageResource(R.drawable.ic_passenger_placeholder);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    public static class ActiveViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, imgPanic;
        TextView tvDeparture, tvDestination, tvPrice, tvStartTime, tvEndTime, tvStatus, tvPassengerName;
        ImageButton btnNext, btnPrev;
        Button btnStart, btnEnd;

        public ActiveViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgPassenger);
            imgPanic = itemView.findViewById(R.id.imgPanic);
            tvDeparture = itemView.findViewById(R.id.txtStartLocation);
            tvDestination = itemView.findViewById(R.id.txtEndLocation);
            tvPrice = itemView.findViewById(R.id.txtPrice);
            tvStartTime = itemView.findViewById(R.id.txtStartTime);
            tvEndTime = itemView.findViewById(R.id.txtEndTime);
            tvStatus = itemView.findViewById(R.id.txtStatus);
            tvPassengerName = itemView.findViewById(R.id.txtPassengerName);
            btnNext = itemView.findViewById(R.id.btnNextPassenger);
            btnPrev = itemView.findViewById(R.id.btnPrevPassenger);
            btnStart = itemView.findViewById(R.id.btnStartRide);
            btnEnd = itemView.findViewById(R.id.btnEndRide);
        }
    }
}