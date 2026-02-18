package com.example.lavugio_mobile.ui.chat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.ChatMessageModel;
import com.example.lavugio_mobile.services.ChatService;
import com.example.lavugio_mobile.services.auth.AuthService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Chat UI for a single conversation.
 *
 * Usage – put this fragment into a container and pass the target user ID:
 *
 *   Bundle args = new Bundle();
 *   args.putInt(MessageBoxFragment.ARG_RECEIVER_ID, userId);
 *   MessageBoxFragment fragment = new MessageBoxFragment();
 *   fragment.setArguments(args);
 *
 * For regular users, receiverId should be 0 (admin).
 * For admins, receiverId is the regular user's ID.
 */
public class MessageBoxFragment extends Fragment {

    public static final String ARG_RECEIVER_ID = "receiver_id";

    private int receiverId;
    private int currentUserId;
    private boolean isAdmin;

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;

    private MessageAdapter adapter;
    private ChatService chatService;
    private LinearLayoutManager layoutManager;

    // ── Factory ───────────────────────────────────────────────────────────────

    public static MessageBoxFragment newInstance(int receiverId) {
        MessageBoxFragment fragment = new MessageBoxFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RECEIVER_ID, receiverId);
        fragment.setArguments(args);
        return fragment;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthService auth = AuthService.getInstance();
        isAdmin = auth.isAdmin();

        // Admin uses senderId=0; regular user uses their actual ID
        currentUserId = isAdmin ? 0 : (auth.getUserId() != null ? auth.getUserId() : 0);

        if (getArguments() != null) {
            receiverId = getArguments().getInt(ARG_RECEIVER_ID, 0);
        }

        chatService = new ChatService(LavugioApp.getWebSocketService());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage  = view.findViewById(R.id.etMessage);
        btnSend    = view.findViewById(R.id.btnSend);

        // RecyclerView setup
        layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true); // newest at bottom
        rvMessages.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(currentUserId, isAdmin);
        rvMessages.setAdapter(adapter);

        // Send button
        btnSend.setOnClickListener(v -> sendMessage());

        // Send on keyboard action
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true;
            }
            return false;
        });

        loadChatHistory();
        connectToChat();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatService.disconnectFromChat();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void loadChatHistory() {

        chatService.getChatHistory(receiverId, new ChatService.Callback<List<ChatMessageModel>>() {
            @Override
            public void onSuccess(List<ChatMessageModel> result) {
                if (!isAdded()) return;
                adapter.setMessages(result);
                scrollToBottom();
            }

            @Override
            public void onError(int code, String message) {
                // Silently ignore – same behaviour as Angular's console.error
            }
        });
    }

    private void connectToChat() {

        chatService.connectToChat(receiverId, new ChatService.Callback<ChatMessageModel>() {
            @Override
            public void onSuccess(ChatMessageModel incoming) {
                if (!isAdded()) return;

                // Deduplicate – mirrors Angular's 'exists' check
                if (!adapter.contains(incoming)) {
                    adapter.addMessage(incoming);
                    scrollToBottom();
                }
            }

            @Override
            public void onError(int code, String message) {
                // Ignore
            }
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        ChatMessageModel message = new ChatMessageModel(
                currentUserId,
                receiverId,
                text,
                LocalDateTime.now()
        );

        chatService.sendMessage(message);
        etMessage.setText("");
        scrollToBottom();
    }

    private void scrollToBottom() {
        rvMessages.post(() -> {
            int count = adapter.getItemCount();
            if (count > 0) {
                rvMessages.smoothScrollToPosition(count - 1);
            }
        });
    }
}