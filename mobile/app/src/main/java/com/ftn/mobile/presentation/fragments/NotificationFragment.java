package com.ftn.mobile.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ftn.mobile.R;
import com.ftn.mobile.data.local.TokenStorage;
import com.ftn.mobile.data.local.UserRoleManger;
import com.ftn.mobile.presentation.adapter.NotificationAdapter;
import com.ftn.mobile.presentation.viewModel.NotificationViewModel;

public class NotificationFragment extends Fragment {
    private NotificationViewModel viewModel;
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = v.findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String token = TokenStorage.get(requireContext());
        Long userId = UserRoleManger.getUserIdFromToken(token);

        viewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        if (userId != null) {
            viewModel.init(userId, requireContext());
        }
        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            adapter = new NotificationAdapter(notifications, notification -> {
                // Mark as read when clicked
                viewModel.markAsRead(notification);
                // Open tracking if rideId exists
                if (notification.getRideId() != null) {
                    openRideTracking(notification.getRideId());
                }
            });
            recyclerView.setAdapter(adapter);
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        return v;
    }

    private void openRideTracking(long rideId) {
        RideTrackingFragment fragment = new RideTrackingFragment();
        Bundle args = new Bundle();
        args.putLong("rideId", rideId);
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
