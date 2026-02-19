package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
import com.ftn.mobile.data.model.User;
import com.ftn.mobile.data.remote.dto.AdminUserDTO;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UViewHolder>{

    private List<AdminUserDTO> users;
    private OnBlockClickListener listener;

    public interface OnBlockClickListener {
        void onToggleBlock(AdminUserDTO user);
    }
    public UserAdapter (List<AdminUserDTO> users, OnBlockClickListener listener){
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_admin, parent, false);
        return new UViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UViewHolder holder, int position) {
        AdminUserDTO user = users.get(position);
        holder.tvEmail.setText(user.getEmail());

        if (user.isBlocked()) {
            holder.btnBlock.setText("UNBLOCK");
            holder.btnBlock.setBackgroundColor(Color.parseColor("#4CAF50")); // Zelena
        } else {
            holder.btnBlock.setText("BLOCK");
            holder.btnBlock.setBackgroundColor(Color.parseColor("#FF4444")); // Crvena
        }

        holder.btnBlock.setOnClickListener(v -> listener.onToggleBlock(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UViewHolder extends RecyclerView.ViewHolder {

        TextView tvEmail;
        Button btnBlock;
        public UViewHolder(@NonNull View itemView) {
            super(itemView);
            btnBlock = itemView.findViewById(R.id.btnBlock);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}