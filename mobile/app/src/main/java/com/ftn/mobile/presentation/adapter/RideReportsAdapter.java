package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;

import java.util.List;

public class RideReportsAdapter extends RecyclerView.Adapter<RideReportsAdapter.RideReportsViewHolder>{

    public static class ReportItem{
        private String email;
        private String reason;
        public ReportItem(String email, String reason) {
            this.email = email;
            this.reason = reason;
        }
    }
    private final Context context;
    private final List<ReportItem> reports;

    public RideReportsAdapter(Context context, List<ReportItem> reports) {
        this.context = context;
        this.reports = reports;
    }
    @NonNull
    @Override
    public RideReportsAdapter.RideReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new RideReportsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RideReportsAdapter.RideReportsViewHolder holder, int position) {
        ReportItem report = reports.get(position);
        holder.tvReportEmail.setText(report.email != null ? report.email : "-");
        holder.tvReportReason.setText(report.reason != null ? report.reason : "-");
    }

    @Override
    public int getItemCount() {
        return reports != null ? reports.size() : 0;
    }

    static class RideReportsViewHolder extends RecyclerView.ViewHolder{
        TextView tvReportEmail, tvReportReason;
        public RideReportsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportEmail = itemView.findViewById(R.id.tvReportEmail);
            tvReportReason = itemView.findViewById(R.id.tvReportReason);
        }

    }
}
