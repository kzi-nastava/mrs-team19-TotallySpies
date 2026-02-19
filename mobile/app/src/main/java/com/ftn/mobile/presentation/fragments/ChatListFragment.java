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
import com.ftn.mobile.presentation.adapter.UserChatAdapter;
import com.ftn.mobile.presentation.viewModel.ChatViewModel;

public class ChatListFragment extends Fragment {
    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private UserChatAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        recyclerView = v.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UserChatAdapter(user -> {
            ChatFragment fragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putBoolean("isAdmin", true);
            args.putLong("userId", user.getId());
            args.putString("userName", user.getName());
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        String token = TokenStorage.get(requireContext());
        Long currentUserId = UserRoleManger.getUserIdFromToken(token);

        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        viewModel.init(currentUserId, true, null);

        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> adapter.setUsers(users));
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        return v;
    }
}