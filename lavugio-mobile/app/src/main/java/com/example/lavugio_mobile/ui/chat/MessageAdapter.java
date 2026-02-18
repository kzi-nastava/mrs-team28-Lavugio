package com.example.lavugio_mobile.ui.chat;

import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.ChatMessageModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for chat message bubbles.
 *
 * isSender logic mirrors Angular Message component:
 *   - ADMIN:          senderId == 0  → isSender
 *   - Regular user:   senderId == userId → isSender
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private static final int COLOR_SENT     = 0xFF283618; // dark green  (sent bubble)
    private static final int COLOR_SENT_TXT = 0xFFFEFAE0; // cream
    private static final int COLOR_RECV     = 0xFFDDA15E; // orange      (received bubble)
    private static final int COLOR_RECV_TXT = 0xFF283618; // dark green

    private final List<ChatMessageModel> messages = new ArrayList<>();
    private final int currentUserId; // 0 for ADMIN, actual id for regular user
    private final boolean isAdmin;

    public MessageAdapter(int currentUserId, boolean isAdmin) {
        this.currentUserId = currentUserId;
        this.isAdmin = isAdmin;
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    public void setMessages(List<ChatMessageModel> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessageModel message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    /** Returns true if a message with the exact same fields already exists. */
    public boolean contains(ChatMessageModel incoming) {
        for (ChatMessageModel m : messages) {
            if (m.getSenderId()   == incoming.getSenderId()   &&
                m.getReceiverId() == incoming.getReceiverId() &&
                safeEquals(m.getText(), incoming.getText())   &&
                safeEquals(m.getTimestamp().toString(), incoming.getTimestamp().toString())) {
                return true;
            }
        }
        return false;
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessageModel message = messages.get(position);
        boolean isSender = checkIsSender(message.getSenderId());
        holder.bind(message.getText(), isSender);
    }

    @Override
    public int getItemCount() { return messages.size(); }

    // ── Helper ────────────────────────────────────────────────────────────────

    private boolean checkIsSender(int senderId) {
        if (isAdmin) {
            return senderId == 0; // admin's own messages have senderId 0
        }
        return senderId == currentUserId;
    }

    private static boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvMessageText);
        }

        void bind(String text, boolean isSender) {
            tvText.setText(text);

            // Gravity
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvText.getLayoutParams();
            params.gravity = isSender ? Gravity.END : Gravity.START;
            tvText.setLayoutParams(params);

            // Colors
            int bgColor  = isSender ? COLOR_SENT     : COLOR_RECV;
            int txtColor = isSender ? COLOR_SENT_TXT : COLOR_RECV_TXT;
            tvText.setTextColor(txtColor);

            // Rounded bubble background
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(bgColor);

            float r = 40f; // corner radius in dp-ish
            if (isSender) {
                // round all corners except bottom-right
                drawable.setCornerRadii(new float[]{r, r, r, r, 4f, 4f, r, r});
            } else {
                // round all corners except bottom-left
                drawable.setCornerRadii(new float[]{r, r, r, r, r, r, 4f, 4f});
            }
            tvText.setBackground(drawable);
        }
    }
}