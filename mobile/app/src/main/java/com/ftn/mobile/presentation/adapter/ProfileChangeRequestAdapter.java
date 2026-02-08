package com.ftn.mobile.presentation.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ftn.mobile.R;
import com.ftn.mobile.data.remote.api.AdminApi;
import com.ftn.mobile.data.remote.dto.ProfileChangeRequestDTO;
import com.ftn.mobile.data.remote.dto.enums.ProfileField;
import com.ftn.mobile.utils.ProfileImageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileChangeRequestAdapter extends RecyclerView.Adapter<ProfileChangeRequestAdapter.ViewHolder> {
    private List<ProfileChangeRequestDTO> requests;
    private AdminApi api;

    public ProfileChangeRequestAdapter(List<ProfileChangeRequestDTO> requests, AdminApi api) {
        this.requests = requests;
        this.api = api;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserEmail, tvEditedField, tvOldValue, tvNewValue;
        ShapeableImageView imgOld, imgNew;
        MaterialButton btnApprove, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvEditedField = itemView.findViewById(R.id.tvEditedField);
            tvOldValue = itemView.findViewById(R.id.tvOldValue);
            tvNewValue = itemView.findViewById(R.id.tvNewValue);
            imgOld = itemView.findViewById(R.id.imgOld);
            imgNew = itemView.findViewById(R.id.imgNew);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }

    @NonNull
    @Override
    public ProfileChangeRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_approval, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileChangeRequestAdapter.ViewHolder holder, int position) {
        ProfileChangeRequestDTO dto = requests.get(position);

        holder.tvUserEmail.setText(dto.getUserEmail());
        holder.tvEditedField.setText(dto.getField().name());

        if (dto.getField() == ProfileField.IMAGE) {
            holder.tvOldValue.setVisibility(View.GONE);
            holder.tvNewValue.setVisibility(View.GONE);
            holder.imgOld.setVisibility(View.VISIBLE);
            holder.imgNew.setVisibility(View.VISIBLE);

            ProfileImageManager imgManagerOld = new ProfileImageManager(
                    (Activity) holder.itemView.getContext(),
                    holder.imgOld,
                    () -> {}
            );
            imgManagerOld.loadImage(dto.getOldValue());

            ProfileImageManager imgManagerNew = new ProfileImageManager(
                    (Activity) holder.itemView.getContext(),
                    holder.imgNew,
                    () -> {}
            );
            imgManagerNew.loadImage(dto.getNewValue());
        } else {
            holder.tvOldValue.setVisibility(View.VISIBLE);
            holder.tvNewValue.setVisibility(View.VISIBLE);
            holder.imgOld.setVisibility(View.GONE);
            holder.imgNew.setVisibility(View.GONE);
            holder.tvOldValue.setText(dto.getOldValue());
            holder.tvNewValue.setText(dto.getNewValue());
        }

        holder.btnApprove.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return; // item više ne postoji

            ProfileChangeRequestDTO currentDto = requests.get(currentPosition);

            api.approve(currentDto.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "Request approved", Toast.LENGTH_SHORT).show();
                        requests.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                    } else {
                        Toast.makeText(v.getContext(), "Failed to approve", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(v.getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnReject.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return; // item više ne postoji

            ProfileChangeRequestDTO currentDto = requests.get(currentPosition);

            api.reject(currentDto.getId()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(v.getContext(), "Request rejected", Toast.LENGTH_SHORT).show();
                        requests.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                    } else {
                        Toast.makeText(v.getContext(), "Failed to reject", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(v.getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
