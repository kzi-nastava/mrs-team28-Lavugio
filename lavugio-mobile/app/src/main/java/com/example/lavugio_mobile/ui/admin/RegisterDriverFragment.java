package com.example.lavugio_mobile.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.data.model.user.DriverRegistrationData;
import com.example.lavugio_mobile.ui.dialog.SuccessDialogFragment;

public class RegisterDriverFragment extends Fragment {
    private DriverRegistrationData registrationData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_driver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registrationData = new DriverRegistrationData();

        // Show driver information fragment first
        showDriverInformation();
    }

    public void showDriverInformation() {
        replaceChildFragment(RegisterDriverUserFragment.newInstance(registrationData));
    }

    public void showVehicleInformation() {
        replaceChildFragment(RegisterDriverVehicleFragment.newInstance(registrationData));
    }

    private void replaceChildFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.childFragmentContainer, fragment)
                .commit();
    }

    public void setRegistrationData(DriverRegistrationData data) {
        this.registrationData = data;
    }

    public void finishRegistration() {
        // TODO: Send data to backend

        SuccessDialogFragment.newInstance(
                "Success",
                "Driver registered successfully!"
        ).show(getParentFragmentManager(), "success_dialog");

        // Go back to previous screen after delay
        new android.os.Handler().postDelayed(() -> {
            getParentFragmentManager().popBackStack();
        }, 2000);
    }
}
