package com.example.lavugio_mobile.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.UserChatModel;
import com.example.lavugio_mobile.services.ChatService;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin-only chat fragment.
 * Shows a Spinner to pick a user, then loads MessageBoxFragment for that user.
 *
 * Mirrors Angular's AdminChatComponent.
 */
public class AdminChatFragment extends Fragment {

    private Spinner spinnerUsers;
    private TextView tvNoSelection;

    private ChatService chatService;
    private final List<UserChatModel> userList = new ArrayList<>();
    private int selectedUserId = -1;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatService = new ChatService(LavugioApp.getWebSocketService());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerUsers  = view.findViewById(R.id.spinnerUsers);
        tvNoSelection = view.findViewById(R.id.tvNoSelection);

        showPlaceholder();
        loadUsers();
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private void loadUsers() {
        chatService.getChattableUsers(new ChatService.Callback<List<UserChatModel>>() {
            @Override
            public void onSuccess(List<UserChatModel> result) {
                if (!isAdded()) return;
                userList.clear();
                userList.addAll(result);
                populateSpinner();
            }

            @Override
            public void onError(int code, String message) {
                // Silently ignore – mirrors Angular console.error
            }
        });
    }

    private void populateSpinner() {
        // First item is a "Select user..." prompt (non-selectable visually)
        List<String> labels = new ArrayList<>();
        labels.add("Select user...");
        for (UserChatModel u : userList) {
            labels.add(u.getEmail());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                labels
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(spinnerAdapter);

        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Prompt item selected – show placeholder
                    selectedUserId = -1;
                    showPlaceholder();
                    return;
                }

                int newUserId = userList.get(position - 1).getUserId();
                if (newUserId == selectedUserId) return; // no change

                selectedUserId = newUserId;
                openChat(selectedUserId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Replace the chat container with a fresh MessageBoxFragment for the selected user.
     * Mirrors Angular's onUserChange() which destroys and recreates the MessageBox.
     */
    private void openChat(int userId) {
        tvNoSelection.setVisibility(View.GONE);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.chatContainer, MessageBoxFragment.newInstance(userId))
                .commit();
    }

    private void showPlaceholder() {
        tvNoSelection.setVisibility(View.VISIBLE);

        // Remove any existing chat fragment
        Fragment existing = getChildFragmentManager().findFragmentById(R.id.chatContainer);
        if (existing != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(existing)
                    .commit();
        }
    }
}