package com.example.lavugio_mobile.ui.driver.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.PassengerTableRow;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {

    private List<PassengerTableRow> passengers;
    private String backendUrl;

    public PassengerAdapter(List<PassengerTableRow> passengers, String backendUrl) {
        this.passengers = passengers;
        this.backendUrl = backendUrl;
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.passenger_item, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        PassengerTableRow passenger = passengers.get(position);
        holder.tvName.setText(passenger.getName());

        // Ako postoji passengerIconName, učitaj sa servera
        if (passenger.getPassengerIconName() != null && !passenger.getPassengerIconName().isEmpty()) {
            String imageUrl = backendUrl + "api/files/images/" + passenger.getPassengerIconName();
            // Koristi Glide ili Picasso za učitavanje
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_person)
                    .circleCrop()
                    .into(holder.imgPassenger);
        } else {
            holder.imgPassenger.setImageResource(R.drawable.ic_person);
        }
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    static class PassengerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPassenger;
        TextView tvName;

        public PassengerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPassenger = itemView.findViewById(R.id.imgPassenger);
            tvName = itemView.findViewById(R.id.tvPassengerName);
        }
    }
}

