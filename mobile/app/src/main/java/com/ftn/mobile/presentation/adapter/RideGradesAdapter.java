package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.rides.RideGradeDTO;

import java.util.List;

public class RideGradesAdapter extends RecyclerView.Adapter<RideGradesAdapter.RideGradesViewHolder> {

    private final Context context;
    private final List<RideGradeDTO> grades;

    public RideGradesAdapter(Context context, List<RideGradeDTO> grades) {
        this.context = context;
        this.grades = grades;
    }

    @NonNull
    @Override
    public RideGradesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_grade, parent, false);
        return new RideGradesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RideGradesViewHolder holder, int position) {
        RideGradeDTO g = grades.get(position);
        holder.tvEmail.setText(g.getEmail() != null ? g.getEmail() : "-");
        holder.tvValue.setText(g.getGrade() + " (" + (g.getGradeType() != null ? g.getGradeType() : "-") + ")");
    }

    @Override
    public int getItemCount() { return grades != null ? grades.size() : 0; }

    static class RideGradesViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail, tvValue;
        RideGradesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvGradeEmail);
            tvValue = itemView.findViewById(R.id.tvGradeValue);
        }
    }
}
