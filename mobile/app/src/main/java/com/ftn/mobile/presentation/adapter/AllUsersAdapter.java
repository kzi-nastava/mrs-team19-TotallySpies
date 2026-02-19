package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.rides.UserDTO;

import java.util.List;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.AllUsersViewHolder> {

    public interface OnViewHistoryClick {
        void onClick(UserDTO user);
    }

    private final Context context;
    private final List<UserDTO> users;
    private final OnViewHistoryClick listener;

    public AllUsersAdapter(Context context, List<UserDTO> users, OnViewHistoryClick listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AllUsersAdapter.AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_user_in_admin_history, parent, false);
        return new AllUsersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllUsersViewHolder h, int position) {
        UserDTO u = users.get(position);

        h.tvUserName.setText(safe(u.getName()));
        h.tvUserLastName.setText(safe(u.getLastName()));
        h.tvUserEmail.setText(safe(u.getEmail()));
        h.tvUserRole.setText(safe(u.getRole()));

        h.btnViewHistory.setOnClickListener(v -> listener.onClick(u));
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    static class AllUsersViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserLastName, tvUserEmail, tvUserRole;
        android.widget.Button btnViewHistory;

        AllUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserLastName = itemView.findViewById(R.id.tvUserLastName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnViewHistory = itemView.findViewById(R.id.btnViewHistoryUser);
        }
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s;
    }
}
