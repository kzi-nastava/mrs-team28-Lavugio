package com.example.lavugio_mobile.ui.profile;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;

public class ChangePasswordFragment extends Fragment {

    public interface OnPasswordChangedListener {
        void onPasswordChanged(String oldPassword, String newPassword);
        void onCancelled();
    }

    private OnPasswordChangedListener listener;

    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnCancel;
    private Button btnChangePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
    }

    private void initViews(View view) {
        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancelled();
                }
                dismissFragment();
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });

        View rootView = getView();
        if (rootView != null) {
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof FrameLayout) {
                        dismissFragment();
                    }
                }
            });
        }
    }

    private void attemptChangePassword() {
        etOldPassword.setError(null);
        etNewPassword.setError(null);
        etConfirmPassword.setError(null);

        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm password is required");
            focusView = etConfirmPassword;
            cancel = true;
        } else if (!confirmPassword.equals(newPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            focusView = etConfirmPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("New password is required");
            focusView = etNewPassword;
            cancel = true;
        } else if (newPassword.length() < 8) {
            etNewPassword.setError("Password must be at least 8 characters");
            focusView = etNewPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(oldPassword)) {
            etOldPassword.setError("Old password is required");
            focusView = etOldPassword;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            if (listener != null) {
                listener.onPasswordChanged(oldPassword, newPassword);
            }
            dismissFragment();
        }
    }

    public void setOnPasswordChangedListener(OnPasswordChangedListener listener) {
        this.listener = listener;
    }

    private void dismissFragment() {
        getParentFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }
}