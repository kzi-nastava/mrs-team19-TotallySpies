package com.ftn.mobile.presentation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UViewHolder>{

    Context context;
    ArrayList<User> users;
    public UserAdapter (Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_user_admin, parent, false);
        return new UViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UViewHolder holder, int position) {
        User currentUser = users.get(position);
        holder.tvEmail.setText(currentUser.getEmail());
        if (position % 2 == 0) {
            holder.imgUser.setImageResource(R.mipmap.page_mock);
        } else {
            holder.imgUser.setImageResource(R.mipmap.page_mock_male);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView tvEmail;
        public UViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }
    }
}