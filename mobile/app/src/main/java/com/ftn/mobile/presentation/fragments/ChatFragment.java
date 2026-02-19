package com.ftn.mobile.presentation.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.ftn.mobile.presentation.adapter.MessageAdapter;
import com.ftn.mobile.presentation.viewModel.ChatViewModel;

public class ChatFragment extends Fragment {
    private ChatViewModel viewModel;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText editMessage;
    private Button buttonSend;
    private Long otherUserId;
    private boolean isAdmin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = v.findViewById(R.id.recyclerViewMessages);
        editMessage = v.findViewById(R.id.editTextMessage);
        buttonSend = v.findViewById(R.id.buttonSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Bundle args = getArguments();
        if (args != null) {
            isAdmin = args.getBoolean("isAdmin", false);
            otherUserId = args.getLong("userId", -1);
            if (otherUserId == -1) otherUserId = null;
            String userName = args.getString("userName", "Chat");
            requireActivity().setTitle(userName);
        }

        String token = TokenStorage.get(requireContext());
        Long currentUserId = UserRoleManger.getUserIdFromToken(token);

        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        viewModel.init(currentUserId, isAdmin, otherUserId);

        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            adapter = new MessageAdapter(messages, currentUserId);
            recyclerView.setAdapter(adapter);
            recyclerView.scrollToPosition(messages.size() - 1);
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        buttonSend.setOnClickListener(view -> {
            String text = editMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                Long receiverId = isAdmin ? otherUserId : null;
                viewModel.sendMessage(text, receiverId);
                editMessage.setText("");
            }
        });

        return v;
    }
}