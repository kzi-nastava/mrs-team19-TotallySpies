package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.Driver;

import java.util.ArrayList;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DViewHolder>{

    Context context;
    ArrayList<Driver> drivers;
    public DriverAdapter (Context context, ArrayList<Driver> drivers){
        this.context = context;
        this.drivers= drivers;
    }

    @NonNull
    @Override
    public DViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_driver_admin, parent, false);
        return new DriverAdapter.DViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DViewHolder holder, int position) {
        Driver currentDriver = drivers.get(position);
        holder.tvDriverName.setText(currentDriver.getUser().getName());
        holder.tvDriverEmail.setText(currentDriver.getUser().getEmail());
        holder.switchActive.setChecked(currentDriver.getUser().isBlocked());
        holder.tvStatus.setText(holder.switchActive.isChecked() ? "Active" : "Inactive");
        holder.tvStatus.setTextColor(holder.switchActive.isChecked() ?
                Color.parseColor("#4CAF50") : Color.parseColor("#F44336"));

        holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.tvStatus.setText("Active");
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            } else {
                holder.tvStatus.setText("Inactive");
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
            }
        });
        if (position % 2 == 0) {
            holder.imgDriver.setImageResource(R.mipmap.page_mock);
        } else {
            holder.imgDriver.setImageResource(R.mipmap.page_mock_male);
        }
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public static class DViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDriver;
        TextView tvDriverName;
        TextView tvDriverEmail;
        TextView tvStatus;
        SwitchCompat switchActive;
        public DViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDriver = itemView.findViewById(R.id.imgDriver);
            tvDriverName = itemView.findViewById(R.id.tvDriverName);
            tvDriverEmail = itemView.findViewById(R.id.tvDriverEmail);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            switchActive = itemView.findViewById(R.id.switchActive);
        }
    }
}