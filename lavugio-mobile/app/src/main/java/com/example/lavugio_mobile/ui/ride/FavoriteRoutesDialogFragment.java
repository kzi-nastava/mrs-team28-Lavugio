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

        loadTestRoutes();

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

    private void loadTestRoutes() {
        // Test route 1: Home to Work
        RideDestination home = new RideDestination(
                1L,
                "Home",
                "Bulevar oslobodjenja",
                "46",
                "Novi Sad",
                "Serbia",
                new Coordinates(19.847400d, 45.267100d)
        );
        RideDestination work = new RideDestination(
                2L,
                "Office",
                "Trg Dositeja Obradovica",
                "6",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.8492d, 45.2551d)
        );
        favoriteRoutes.add(new FavoriteRoute(1L, "Home to Work", new RideDestination[]{home, work}));

        // Test route 2: Shopping route
        RideDestination start = new RideDestination(
                3L,
                "Apartment",
                "Bulevar Cara Lazara",
                "10",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.842d, 45.265d)
        );
        RideDestination mall = new RideDestination(
                4L,
                "BIG Shopping Center",
                "Bulevar oslobodjenja",
                "119",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.8052d, 45.2398d)
        );
        RideDestination groceryStore = new RideDestination(
                5L,
                "Mercator",
                "Sutjeska",
                "2",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.8335d, 45.259d)
        );
        favoriteRoutes.add(new FavoriteRoute(2L, "Shopping Route", new RideDestination[]{start, mall, groceryStore}));

        // Test route 3: University
        RideDestination dorm = new RideDestination(
                6L,
                "Student Dorm",
                "Narodnih heroja",
                "23",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.845d, 45.258d)
        );
        RideDestination university = new RideDestination(
                7L,
                "Faculty of Technical Sciences",
                "Trg Dositeja Obradovica",
                "6",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.8492d, 45.2551d)
        );
        favoriteRoutes.add(new FavoriteRoute(3L, "To University", new RideDestination[]{dorm, university}));

        // Test route 4: Weekend trip
        RideDestination city = new RideDestination(
                8L,
                "City Center",
                "Zmaj Jovina",
                "5",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.8432d, 45.2555d)
        );
        RideDestination fortress = new RideDestination(
                9L,
                "Petrovaradin Fortress",
                "Petrovaradinska tvrdava",
                "bb",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.8634d, 45.2515d)
        );
        RideDestination beach = new RideDestination(
                10L,
                "Štrand Beach",
                "Ribarsko ostrvo",
                "bb",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.849d, 45.251d)
        );
        favoriteRoutes.add(new FavoriteRoute(4L, "Weekend Sightseeing", new RideDestination[]{city, fortress, beach}));

        // Test route 5: Airport run
        RideDestination hotel = new RideDestination(
                11L,
                "Hotel Park",
                "Bulevar oslobodjenja",
                "23",
                "Novi Sad",
                "Serbia",
            new Coordinates(19.84d, 45.26d)
        );
        RideDestination airport = new RideDestination(
                12L,
                "Belgrade Airport",
                "Airport Road",
                "1",
                "Belgrade",
                "Serbia",
                new Coordinates(20.309000, 44.818500)
        );
        favoriteRoutes.add(new FavoriteRoute(5L, "To Airport", new RideDestination[]{hotel, airport}));
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