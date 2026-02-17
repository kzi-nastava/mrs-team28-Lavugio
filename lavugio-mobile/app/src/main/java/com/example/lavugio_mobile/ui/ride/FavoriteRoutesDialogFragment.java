package com.example.lavugio_mobile.ui.ride;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.route.Coordinates;
import com.example.lavugio_mobile.data.model.route.FavoriteRoute;
import com.example.lavugio_mobile.data.model.route.RideDestination;

import java.util.ArrayList;
import java.util.List;

public class FavoriteRoutesDialogFragment extends DialogFragment {

    // Interface for callback
    public interface OnRouteSelectedListener {
        void onRouteSelected(RideDestination[] destinations, String routeName);
    }

    private OnRouteSelectedListener listener;
    private String selectedRouteId;
    private List<FavoriteRoute> favoriteRoutes = new ArrayList<>();

    private LinearLayout llRoutesList;
    private TextView tvNoRoutes;
    private Button btnCancel;
    private Button btnSelect;

    public static FavoriteRoutesDialogFragment newInstance(List<FavoriteRoute> routes) {
        FavoriteRoutesDialogFragment fragment = new FavoriteRoutesDialogFragment();
        fragment.favoriteRoutes = routes;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void dismissFragment() {
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_routes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();

        view.findViewById(R.id.cvRouteItem).setVisibility(View.GONE);

        updateRoutesUI();
    }

    private void initViews(View view) {
        llRoutesList = view.findViewById(R.id.llRoutesList);
        tvNoRoutes = view.findViewById(R.id.tvNoRoutes);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSelect = view.findViewById(R.id.btnSelect);
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFragment();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRouteId != null && listener != null) {
                    // Find selected route
                    for (FavoriteRoute route : favoriteRoutes) {
                        if (route.getId().equals(selectedRouteId)) {
                            listener.onRouteSelected(
                                    route.getDestinations(),
                                    route.getName()
                            );
                            break;
                        }
                    }
                }
                dismissFragment();
            }
        });
    }

    private void updateRoutesUI() {
        if (favoriteRoutes.isEmpty()) {
            tvNoRoutes.setVisibility(View.VISIBLE);
            btnSelect.setEnabled(false);
            btnSelect.setAlpha(0.5f);
        } else {
            tvNoRoutes.setVisibility(View.GONE);
            displayRoutes(favoriteRoutes);

            selectedRouteId = null;
            btnSelect.setEnabled(false);
            btnSelect.setAlpha(0.5f);
        }
    }

    private void displayRoutes(List<FavoriteRoute> routes) {
        llRoutesList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (FavoriteRoute route : routes) {
            View itemView = inflater.inflate(R.layout.item_favorite_route, llRoutesList, false);

            CardView cvRouteCard = itemView.findViewById(R.id.cvRouteItem);
            TextView tvRouteName = itemView.findViewById(R.id.tvRouteName);
            TextView tvDestinationsCount = itemView.findViewById(R.id.tvDestinationsCount);
            TextView tvFromAddress = itemView.findViewById(R.id.tvFromAddress);
            TextView tvToAddress = itemView.findViewById(R.id.tvToAddress);

            tvRouteName.setText(route.getName());
            tvDestinationsCount.setText(route.getDestinationsCount() + " destinations");
            tvFromAddress.setText(route.getFromAddress());
            tvToAddress.setText(route.getToAddress());

            // ID of route is tag
            cvRouteCard.setTag(route.getId());

            cvRouteCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String routeId = (String) v.getTag();
                    selectedRouteId = routeId;
                    updateSelection(routeId);

                    // Omogući Select dugme
                    btnSelect.setEnabled(true);
                    btnSelect.setAlpha(1f);
                }
            });

            llRoutesList.addView(itemView);
        }
    }

    private void updateSelection(String selectedId) {
        for (int i = 0; i < llRoutesList.getChildCount(); i++) {
            View child = llRoutesList.getChildAt(i);
            CardView cvRouteCard = child.findViewById(R.id.cvRouteItem);

            if (cvRouteCard.getTag() != null && cvRouteCard.getTag().equals(selectedId)) {
                cvRouteCard.setCardBackgroundColor(Color.parseColor("#E8F5E8"));
            } else {
                cvRouteCard.setCardBackgroundColor(Color.WHITE);
            }
        }
    }

    public void setOnRouteSelectedListener(OnRouteSelectedListener listener) {
        this.listener = listener;
    }
}