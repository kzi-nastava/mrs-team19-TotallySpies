package com.ftn.mobile.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.dto.UserChatDTO;
import java.util.ArrayList;
import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.ViewHolder> {
    private List<UserChatDTO> users = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserChatDTO user);
    }

    public UserChatAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<UserChatDTO> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserChatDTO user = users.get(position);
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
    }

    @Override
    public int getItemCount() { return users.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;
        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.textViewName);
            email = v.findViewById(R.id.textViewEmail);
        }
    }
}