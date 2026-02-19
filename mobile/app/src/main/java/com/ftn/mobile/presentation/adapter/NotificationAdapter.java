package com.ftn.mobile.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.NotificationDTO;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<NotificationDTO> notifications;
    private OnItemClickListener listener;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
    private DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(NotificationDTO notification);
    }

    public NotificationAdapter(List<NotificationDTO> notifications, OnItemClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationDTO n = notifications.get(position);
        holder.message.setText(n.getMessage());
        if (n.getCreatedAt() != null) {
            try {
                OffsetDateTime dateTime = OffsetDateTime.parse(n.getCreatedAt(), inputFormatter);
                holder.time.setText(dateTime.format(outputFormatter));
            } catch (DateTimeParseException e) {
                holder.time.setText("");
            }
        }
        if (!n.isRead()) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getColor(R.color.card_color));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getColor(android.R.color.transparent));
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(n));
    }

    @Override
    public int getItemCount() { return notifications.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, time;
        ViewHolder(View v) {
            super(v);
            message = v.findViewById(R.id.textViewMessage);
            time = v.findViewById(R.id.textViewTime);
        }
    }
}