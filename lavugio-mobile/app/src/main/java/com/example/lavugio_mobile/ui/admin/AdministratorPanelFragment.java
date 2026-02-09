package com.example.lavugio_mobile.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

public class AdministratorPanelFragment extends Fragment {
    private Button btnRegisterDriver;
    private Button btnDriverUpdateRequests;
    private Button btnBlockUser;
    private Button btnSeeReports;
    private Button btnPanicAlerts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        // Initialize buttons
        btnRegisterDriver = view.findViewById(R.id.btnRegisterDriver);
        btnDriverUpdateRequests = view.findViewById(R.id.btnDriverUpdateRequests);
        btnBlockUser = view.findViewById(R.id.btnBlockUser);
        btnSeeReports = view.findViewById(R.id.btnSeeReports);
        btnPanicAlerts = view.findViewById(R.id.btnPanicAlerts);

        // Set click listeners
        btnRegisterDriver.setOnClickListener(v -> {
            // TODO: Navigate to Register Driver screen when implemented
        });

        btnDriverUpdateRequests.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new DriverUpdateRequestsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnBlockUser.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new BlockUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnSeeReports.setOnClickListener(v -> {
            // TODO: Navigate to Reports screen when implemented
        });

        btnPanicAlerts.setOnClickListener(v -> {
            // TODO: Navigate to Panic Alerts screen when implemented
        });

        return view;
    }
}
