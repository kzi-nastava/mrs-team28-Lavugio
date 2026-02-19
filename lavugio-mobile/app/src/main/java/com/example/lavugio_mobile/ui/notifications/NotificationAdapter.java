package com.example.lavugio_mobile.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.NotificationModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClicked(NotificationModel notification);
    }

    private final List<NotificationModel> notifications;
    private final OnNotificationClickListener listener;

    // Odgovara Angular date pipe: 'dd.MM.yyyy HH:mm'
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public NotificationAdapter(List<NotificationModel> notifications,
                               OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTextView;
        private final TextView textTextView;
        private final TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textNotificationTitle);
            textTextView  = itemView.findViewById(R.id.textNotificationBody);
            dateTextView  = itemView.findViewById(R.id.textNotificationDate);
        }

        public void bind(NotificationModel notification, OnNotificationClickListener listener) {
            titleTextView.setText(notification.getTitle());
            textTextView.setText(notification.getText());

            // LocalDateTime → "dd.MM.yyyy HH:mm"
            LocalDateTime date = notification.getSendDate();
            dateTextView.setText(date != null ? date.format(OUTPUT_FORMAT) : "");

            if (notification.getLink() != null && !notification.getLink().isEmpty()) {
                itemView.setOnClickListener(v -> listener.onNotificationClicked(notification));
                itemView.setAlpha(1f);
            } else {
                itemView.setOnClickListener(null);
                itemView.setAlpha(0.9f);
            }
        }
    }
}